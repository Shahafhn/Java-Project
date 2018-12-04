package project.serverside.the_game.monsters;

public class ChestMonster extends Monster {

    public ChestMonster(long maxHP, long loot) {
        super(maxHP * 3, loot * 6);
        super.setName("Mr. ChestMonster");
    }
}
