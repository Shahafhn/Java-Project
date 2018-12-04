package project.serverside.the_game;

import project.serverside.ClientConnection;
import project.serverside.the_game.monsters.Monster;

class GameInitializer {
    private GameManageable gameManageable;
    private ClientConnection clientConnection;
    boolean isLoaded = false;
    long incrementalHP;
    long incrementalLoot;
    int level;
    int stage;
    long clickPrice;
    long assistPrice;
    int heroCount;
    int assistCount;
    Hero hero;
    AssistantThread assistantThread;
    Monster monster;

    GameInitializer(GameManageable gameManageable, ClientConnection clientConnection) {
        this.gameManageable = gameManageable;
        this.clientConnection = clientConnection;
    }

    void startOver(){
        hero = new Hero(gameManageable);
        incrementalHP = 10;
        incrementalLoot = 3;
        stage = 1;
        level = 1;
        clickPrice = 4;
        assistPrice = 350;
        heroCount = 1;
        assistCount = 1;
    }

    void loadProgress(){
        long heroDamage = clientConnection.getNextLong();
        long assistDamage = clientConnection.getNextLong();
        int assistSpeed = clientConnection.getNextInt();
        long coins = clientConnection.getNextLong();
        int diamonds = clientConnection.getNextInt();
        hero = new Hero(heroDamage,coins,diamonds,assistDamage,assistSpeed, gameManageable);
        clickPrice = clientConnection.getNextLong();
        assistPrice = clientConnection.getNextLong();
        level = clientConnection.getNextInt();
        stage = clientConnection.getNextInt();
        incrementalHP = clientConnection.getNextLong();
        incrementalLoot = clientConnection.getNextLong();
        heroCount = clientConnection.getNextInt();
        assistCount = clientConnection.getNextInt();
        isLoaded = validateSave();
    }

    private boolean validateSave(){         //checks if the uploaded progress is valid
        if (hero.validate())
            if (monster.validate())
                if (clickPrice > 0)
                    if (assistPrice > 0)
                        if (level > 0)
                            if (stage > 0)
                                if (incrementalHP > 0)
                                    if (incrementalLoot > 0)
                                        if (heroCount > 0)
                                            return assistCount > 0;
        return false;
    }


}
