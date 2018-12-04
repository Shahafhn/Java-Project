package project.serverside.the_game;

import project.serverside.FinalActions;
import project.serverside.ClientConnection;

class Hero extends Character{
    private long coins;
    private int diamonds;
    private Assistant assistant;
    private AssistantAttackable assistantAttackable;
    private GameManageable gameManageable;

    Hero(long damage, long coins, int diamonds, long assistDamage, int assistSpeed, GameManageable gameManageable){
        super(damage);
        this.coins = coins;
        this.diamonds = diamonds;
        this.gameManageable = gameManageable;
        assistant = new Assistant(assistDamage,assistSpeed);
    }

    Hero(GameManageable gameManageable) {       //new game
        this(1,0,0,0,0, gameManageable);
    }

    @Override
    public long getDamage() {
        return super.getDamage();
    }

    void upgradeDamage(int counter) {
        super.upgradeDamage(getUpgradePreview(counter));
    }

    long getUpgradePreview(int counter){
        if (counter % 5 == 0)
            return getDamage() + (getDamage()/2 + 1);
        return getDamage()/2 + 1;
    }

    long getCoins() {
        return coins;
    }

    int getDiamonds() {
        return diamonds;
    }

    void pickLoot(long loot){
        coins += loot;
    }

    void payWithCoins(long price){
        coins -= price;
    }

    void pickBossLoot(){
        this.diamonds ++;
    }

    void payWithDiamonds(int price){
        diamonds -= price;
    }


    void setAssistant(AssistantAttackable assistantAttackable){
        if (!hasAssist())
            assistant.setAssistant();
        this.assistantAttackable = assistantAttackable;
    }

    boolean hasAssist(){
        return assistant.exists();
    }

    long getAssistDamage(){
        return assistant.getDamage();
    }

    int getAssistSpeed(){
        return assistant.getAttackSpeed();
    }

    void upgradeAssist(int counter){
        assistant.upgradeDamage(counter);
    }

    void upgradeAssistSpeed(){
        assistant.upgradeAttackSpeed();
    }

    void assistAttack(){
        gameManageable.assistantAttack();
    }


    void pauseAssistant(boolean pause){
        if (hasAssist())
            assistantAttackable.pauseAssistant(pause);
    }

    void toggleAssistant(){
        assistantAttackable.toggleAssistant();
    }

    void interruptAssistant(){
        assistantAttackable.interruptAssistant();
    }


    void sendInfo(ClientConnection clientConnection, int heroCount, int assistCount, boolean print){
        clientConnection.sendLong(getDamage());
        clientConnection.sendLong(assistant.getDamage());
        clientConnection.sendInt(assistant.getAttackSpeed());
        if (print) {
            clientConnection.sendLong(getUpgradePreview(heroCount));
            clientConnection.sendLong(assistant.getUpgradePreview(assistCount));
        }

    }

    void sendInventory(ClientConnection clientConnection, boolean print){
        clientConnection.sendLong(coins);
        clientConnection.sendInt(diamonds);
        if (print)
            clientConnection.sendBytes(assistant.exists() ? FinalActions.GOT_ASSISTANT : 1);

    }

    boolean validate(){
        if (getAssistSpeed() >= 0)
            if (getAssistDamage() >= 0)
                if (getDamage() > 0)
                    if (diamonds >= 0)
                        return coins >= 0;
        return false;
    }
}
