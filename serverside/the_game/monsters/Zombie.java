package project.serverside.the_game.monsters;

public class Zombie extends Monster {

    public Zombie(long maxHP, long loot) {
        super((long)(maxHP * 1.3), (long)(loot * 1.3));
        super.setName("Mr. Zombie");
    }
}
