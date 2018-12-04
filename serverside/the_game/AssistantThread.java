package project.serverside.the_game;

class AssistantThread extends Thread implements AssistantAttackable{
    private Hero hero;
    private boolean pause = false;
    private boolean toggleOff = false;

    AssistantThread(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                while(!pause) {
                    sleep(hero.getAssistSpeed());
                    if (!toggleOff && !pause)       //attacks the monster once per attack speed
                        hero.assistAttack();
                    else
                        sleep(1000);
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Assistant Thread closed.");
        }
    }


    @Override
    public void pauseAssistant(boolean pause) {
        this.pause = pause;
    }

    @Override
    public void toggleAssistant() {
        toggleOff = !toggleOff;
        pause = false;
    }

    @Override
    public void interruptAssistant() {
        pause = true;
        interrupt();
    }
}
