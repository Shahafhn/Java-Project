package project.serverside.the_game.monsters;

public class Slime extends Monster {

    public Slime(long maxHP, long loot) {
        super((long)(maxHP * 0.5), (long)(loot * 0.5));
        super.setName("Mr. Slime");
    }
}
