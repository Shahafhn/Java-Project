package project.clientside.user_threads;

import project.clientside.FinalActions;
import project.clientside.ServerConnection;

public class PrintThread extends Thread implements OnClickListener {
    private ServerConnection serverConnection;
    private boolean pause = false;
    private boolean toggleOff = false;

    public PrintThread(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                while (!pause) {        //while not paused
                    sleep(1000);
                    if (!toggleOff && !pause)       //while toggled on
                                                    //prints battle information once per 2 seconds
                        serverConnection.sendBytes(FinalActions.BATTLE_PRINT);
                        sleep(1000);
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Closing thread.");
        }
    }

    @Override
    public void pauseAutoBattlePrinter(boolean pause) {     //pausing if user is on main menu
        this.pause = pause;
    }

    @Override
    public void toggleAutoBattlePrinter() {               //toggle printer on/off
        toggleOff = !toggleOff;
    }

    @Override
    public void interruptAutoBattlePrinter() {           //kill thread
        pause = true;
        interrupt();
    }
}
