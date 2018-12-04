package project.serverside.the_game.monsters;

import project.serverside.ClientConnection;

public class Monster {
    private long maxHP;
    private long currentHP;
    private long loot;
    private String name;

    public Monster(long maxHP, long loot) {
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.loot = loot;
    }

    public long getCurrentHP() {
        if (currentHP < 0)
            return 0;
        return currentHP;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void attackMonster(long damage) {
        currentHP -= damage;

    }

    public void sendInfo(ClientConnection clientConnection, boolean print) {
        if (!print)
            return;
        clientConnection.sendBytes(name);
        clientConnection.sendLong(maxHP);
        clientConnection.sendLong(currentHP);
        clientConnection.sendLong(loot);

    }

    public long dropLoot() {
        return loot;
    }

    public boolean validate(){
        if (name != null)
            if (maxHP > 0)
                return loot > 0;
        return false;
    }
}
