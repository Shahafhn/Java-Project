package project.serverside.the_game;

import project.serverside.FinalActions;
import project.serverside.ClientConnection;
import project.serverside.the_game.monsters.*;

import java.util.Random;

public class GameManager implements GameManageable {
    private GameInitializer gameData;
    private ClientConnection clientConnection;


    public GameManager(ClientConnection clientConnection){
        this.clientConnection = clientConnection;
        gameData = new GameInitializer(this,clientConnection);
    }


    public void startOver(){        //new game
        gameData.startOver();       //resets gameData variables
        clientConnection.sendBytes(FinalActions.GAME_STARTED);
        createMonster();            //new monster
        BattleSender.sendBattle(true,clientConnection,gameData);    //prints battle
    }


    public void loadProgress(){     //attempting to load progress
        gameData.loadProgress();
        createMonster();
        if (gameData.isLoaded) {
            clientConnection.sendBytes(FinalActions.GAME_STARTED);
            if (gameData.hero.hasAssist()) {
                clientConnection.sendBytes(FinalActions.GOT_ASSISTANT);
                gameData.assistantThread = new AssistantThread(gameData.hero);
                gameData.hero.setAssistant(gameData.assistantThread);
                gameData.assistantThread.start();
            }
            BattleSender.sendBattle(true, clientConnection, gameData);
        }
        else
            clientConnection.sendBytes(FinalActions.ILLEGAL_SAVE);
    }

    public void heroAttack(){
        gameData.monster.attackMonster(gameData.hero.getDamage());
        checkOnMonster();
        BattleSender.sendBattle(true,clientConnection,gameData);
    }

    @Override
    public void assistantAttack(){
        gameData.monster.attackMonster(gameData.hero.getAssistDamage());
        checkOnMonster();
    }

    public void upgradeClick(){
        if (gameData.hero.getCoins() >= gameData.clickPrice){
            gameData.hero.payWithCoins(gameData.clickPrice);
            gameData.hero.upgradeDamage(gameData.heroCount);
            gameData.heroCount++;
            gameData.clickPrice += gameData.hero.getUpgradePreview(gameData.heroCount) * 2;
            BattleSender.sendBattle(true,clientConnection,gameData);
        }else {
            BattleSender.printError(false,clientConnection,gameData);
        }
    }

    public void upgradeAssist(){
        if (!gameData.hero.hasAssist()) {
            if (gameData.hero.getDiamonds() >= 2) {
                gameData.hero.payWithDiamonds(2);
                gameData.assistantThread = new AssistantThread(gameData.hero);
                gameData.hero.setAssistant(gameData.assistantThread);
                gameData.assistantThread.start();
                BattleSender.sendBattle(true,clientConnection,gameData);
                clientConnection.sendBytes(FinalActions.GOT_ASSISTANT);
            }else
                BattleSender.printError(true,clientConnection,gameData);
        }else if (gameData.hero.getCoins() >= gameData.assistPrice) {
            gameData.hero.payWithCoins(gameData.assistPrice);
            gameData.hero.upgradeAssist(gameData.assistCount);
            gameData.assistCount++;
            gameData.assistPrice *= 1.6;
            BattleSender.sendBattle(true,clientConnection,gameData);
        }else
            BattleSender.printError(false,clientConnection,gameData);
    }

    public void upgradeAttackSpeed(){
        if (gameData.hero.getDiamonds() >= 4 && gameData.hero.hasAssist()) {
            gameData.hero.payWithDiamonds(4);
            gameData.hero.upgradeAssistSpeed();
            BattleSender.sendBattle(true,clientConnection,gameData);
        }
        else
            BattleSender.printError(true,clientConnection,gameData);
    }

    private void checkOnMonster(){  //checks if monster died
        if (gameData.monster.getCurrentHP() == 0){
            gameData.hero.pickLoot(gameData.monster.dropLoot());
            gameData.stage++;
            createMonster();
            if (gameData.stage == 6){
                gameData.hero.pickBossLoot();
                gameData.stage = 1;
                gameData.level++;
                gameData.incrementalHP *= 2;
                gameData.incrementalLoot *= 2;
            }
            createMonster();
        }
    }

    public void saveProgress(){         //attempting to save progress.
        if (gameData.hero.hasAssist())
            interruptAssistant();       //kill assistant thread
        clientConnection.sendBytes(FinalActions.SAVE_PROGRESS);
        BattleSender.sendBattle(false,clientConnection,gameData);   //print = false for full progress
        clientConnection.sendBytes(FinalActions.SAVED_SUCCESSFULLY);
    }

    public void sendBattle(boolean print){
        BattleSender.sendBattle(print,clientConnection,gameData);
    }

    public void pauseAssistant(boolean pause){
        gameData.hero.pauseAssistant(pause);
        if (!pause)
            BattleSender.sendBattle(true,clientConnection,gameData);
    }

    public void toggleAssistant(){
        gameData.hero.toggleAssistant();
        pauseAssistant(false);
    }


    public void interruptAssistant(){
        if (gameData.hero.hasAssist())
            gameData.hero.interruptAssistant();
    }
    @Override
    public void createMonster(){            //if boss, multiply by ↓
        long tempHP = gameData.stage == 5 ? gameData.incrementalHP * 5 : gameData.incrementalHP;
        long tempLoot = gameData.stage == 5 ? gameData.incrementalLoot * 7 : gameData.incrementalLoot;
        Random random = new Random(System.currentTimeMillis());
        switch (random.nextInt(30)){
            case 6: case 7: case 8: case 9: case 10:
                gameData.monster = new Zombie(tempHP,tempLoot);
                break;
            case 11: case 12: case 13: case 14: case 15:
                gameData.monster = new Skeleton(tempHP,tempLoot);
                break;
            case 16: case 17: case 18: case 19:
                gameData.monster = new Orc(tempHP,tempLoot);
                break;
            case 20: case 21: case 22: case 23:
                gameData.monster = new Slime(tempHP,tempLoot);
                break;
            case 24: case 25:
                gameData.monster = new Creeper(tempHP,tempLoot);
                break;
            case 26: case 27:
                gameData.monster = new ChestMonster(tempHP,tempLoot);
                break;
            default:
                gameData.monster = new Goblin(tempHP,tempLoot);
                break;
        }
    }
}
