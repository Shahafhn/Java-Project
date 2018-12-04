package project.serverside.the_game;

class Assistant  extends Character {
    private int attackSpeed;
    private boolean exists;

    Assistant(long damage, int attackSpeed){
        super(damage);
        this.attackSpeed = attackSpeed;
        exists = attackSpeed > 0;
    }

    @Override
    public long getDamage() {
        return super.getDamage();
    }

    int getAttackSpeed() {
        return attackSpeed;
    }

    long getUpgradePreview(int counter){
        if (counter % 5 == 0)
            return getDamage() + (long)(getDamage()*0.5);
        return (long)(getDamage()*0.5);
    }

    void upgradeDamage(int counter) {
        super.upgradeDamage(getUpgradePreview(counter));

    }

    void upgradeAttackSpeed(){
        attackSpeed *= 0.9;
    }

    void setAssistant(){
        exists = true;
        attackSpeed = 1000;
        super.upgradeDamage(100);
    }

    public boolean exists(){
        return exists;
    }

}