package project.serverside.the_game.monsters;

public class Orc extends Monster {

    public Orc(long maxHP, long loot) {
        super(maxHP * 2, loot * 2);
        super.setName("Mr. Orc");
    }
}
