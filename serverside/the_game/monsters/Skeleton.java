package project.serverside.the_game.monsters;

public class Skeleton extends Monster {

    public Skeleton(long maxHP, long loot) {
        super((long)(maxHP * 1.5), (long)(loot * 1.5));
        super.setName("Mr. Skeleton");
    }
}
