package project.serverside.the_game;

class Character {
    private long damage;

    Character(long damage){
        this.damage = damage;
    }

    public long getDamage(){
        return damage;
    }

    void upgradeDamage(long increase){
        damage += increase;
    }

}