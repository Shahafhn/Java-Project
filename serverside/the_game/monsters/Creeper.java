package project.serverside.the_game.monsters;

public class Creeper extends Monster {

    public Creeper(long maxHP, long loot) {
        super((long) (maxHP * 2.5), (long) (loot * 0.5));
        super.setName("Mr. Creeper");
    }
}
