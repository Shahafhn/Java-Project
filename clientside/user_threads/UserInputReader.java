package project.clientside.user_threads;

import project.clientside.BattlePrinter;
import project.clientside.FinalActions;
import project.clientside.FirstRun;
import project.clientside.ServerConnection;

public class UserInputReader extends Thread{
    private ServerConnection serverConnection;
    private OnClickListener onClickListener;

    public UserInputReader(ServerConnection serverConnection,OnClickListener onClickListener){
        this.serverConnection = serverConnection;
        this.onClickListener = onClickListener;
    }


    @Override
    public void run() {
        while (!interrupted()){     //while not interrupted: read next user action
            switch (FirstRun.getNextUserSelection()){
                case FinalActions.CLICK:
                    serverConnection.sendBytes(FinalActions.CLICK);
                    break;
                case 1:
                    serverConnection.sendBytes(FinalActions.UPGRADE_CLICK);
                    break;
                case 2:
                    serverConnection.sendBytes(FinalActions.UPGRADE_ASSISTANT);
                    break;
                case 3:
                    serverConnection.sendBytes(FinalActions.UPGRADE_SPEED);
                    break;
                case 9:                       //main menu
                    onClickListener.pauseAutoBattlePrinter(true);      //pausing auto printer
                    serverConnection.sendBytes(FinalActions.MAIN_MENU);
                    menu();
                    break;
            }
        }
    }

    private void menu(){
            BattlePrinter.mainMenuPrint();
            switch (FirstRun.getNextUserSelection()){
                case 1:
                    System.out.println("Resuming.");
                    serverConnection.sendBytes(FinalActions.RESUME_GAME);
                    onClickListener.pauseAutoBattlePrinter(false);      //resuming auto printer
                    break;
                case 2:
                    serverConnection.sendBytes(FinalActions.TOGGLE_ASSIST);
                    onClickListener.toggleAutoBattlePrinter();          //toggle auto printer on/off
                    onClickListener.pauseAutoBattlePrinter(false);      //resuming auto printer(unrelated to toggle)
                    break;
                case 7:
                    newGame();
                    break;
                case 9:
                    serverConnection.sendBytes(FinalActions.SAVE_PROGRESS);
                    onClickListener.interruptAutoBattlePrinter();       //kill auto printer thread
                    interrupt();                                        //kill this thread
                    break;
                case 0:
                    serverConnection.sendBytes(FinalActions.CRASH_GAME);    //exit without save
                    serverConnection.crashConnection();
                    onClickListener.interruptAutoBattlePrinter();       //kill auto printer thread
                    interrupt();                                        //kill this thread
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.\n");
                    menu();
            }
    }
    private void newGame(){     //sure you want to start a new game?
        BattlePrinter.newGame();
        switch (FirstRun.getNextUserSelection()){
            case 1:             //no
                menu();
                break;
            case 9:             //yes
                onClickListener.interruptAutoBattlePrinter();
                serverConnection.sendBytes(FinalActions.NEW_GAME);
                interrupt();
                break;
            default:
                System.out.println("Invalid selection. Please try again.\n");
                newGame();
        }
    }
}
