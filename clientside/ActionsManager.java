package project.clientside;

import project.clientside.user_threads.PrintThread;
import project.clientside.user_threads.UserInputReader;

class ActionsManager {
    private ServerConnection serverConnection;
    private ImportExport importExport;
    private PrintThread printThread;

    ActionsManager(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    void onFirstLaunch() {
        String username;
        username = FirstRun.onLaunch(serverConnection);     //attempting to login
        if(username == null) {
            serverConnection.crashConnection();
            return;
        }           //if login successful: looking for save file
        importExport = new ImportExport(serverConnection,username);
        importExport.initiateSaveFile();
    }

    void gotAction(int action) {
        switch (action) {
            case FinalActions.BATTLE_PRINT:     //prints full battle information
                BattlePrinter.battlePrint(serverConnection);
                break;
            case FinalActions.ERROR_MESSAGE:    //prints error message
                BattlePrinter.printError(serverConnection);
                break;
            case FinalActions.GOT_ASSISTANT:    //starting automatic printer
                printThread.start();
                break;
            case FinalActions.SAVE_PROGRESS:    //saving progress from server
                importExport.saveProgress();
                serverConnection.crashConnection();
                break;
            case FinalActions.GAME_STARTED:     //starting the game
                printThread = new PrintThread(serverConnection);
                UserInputReader userInputReader = new UserInputReader(serverConnection, printThread);
                userInputReader.start();        //user input thread to get user attacks while reading server actions
                break;
            case FinalActions.ILLEGAL_SAVE:     //server failed to read save file
                importExport.illegalFile();
                break;
            default:                            //unexpected action
                serverConnection.crashConnection();
        }
    }
}