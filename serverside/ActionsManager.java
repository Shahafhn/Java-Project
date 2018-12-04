package project.serverside;

import project.serverside.the_game.GameManager;
import project.serverside.user_related.UserDatabase;
import project.serverside.user_related.UserManager;

class ActionsManager {

    private ClientConnection clientConnection;
    private UserManager userManager;
    private GameManager gameManager;

    ActionsManager(ClientConnection clientConnection, UserDatabase userDatabase) {
        this.clientConnection = clientConnection;
        this.userManager = new UserManager(userDatabase, clientConnection);
    }

    void onFirstLaunch() {      //attempting to login
        int action = clientConnection.getNextAction();
        if (action != FinalActions.LOGIN && action != FinalActions.CREATE_USER) {
            clientConnection.crashConnection();
            return;
        }
        action = userManager.userSignIn(action);
        if (action != FinalActions.SUCCESSFUL_LOGIN && action != FinalActions.SUCCESSFULLY_CREATED) {
            onFirstLaunch();        //if unsuccessful login, tries again
            return;
        }   //if login successful, continuing progress on starting a new game
        gameManager = new GameManager(clientConnection);
        action = clientConnection.getNextAction();
        switch (action) {
            case FinalActions.START_OVER:       //new game
                gameManager.startOver();
                break;
            case FinalActions.CONTINUE:         //read progress file
                gameManager.loadProgress();
                break;
            default:        //unexpected action
                clientConnection.crashConnection();
        }
    }

    void gotAction(int action) {
        switch (action) {
            case FinalActions.BATTLE_PRINT:
                gameManager.sendBattle(true);
                break;
            case FinalActions.CLICK:
                gameManager.heroAttack();
                break;
            case FinalActions.UPGRADE_CLICK:
                gameManager.upgradeClick();
                break;
            case FinalActions.UPGRADE_ASSISTANT:
                gameManager.upgradeAssist();
                break;
            case FinalActions.UPGRADE_SPEED:
                gameManager.upgradeAttackSpeed();
                break;
            case FinalActions.MAIN_MENU:
                mainMenu();
                break;
            default:        //unexpected action
                clientConnection.crashConnection();
        }
    }
    private void mainMenu(){        //user is in main menu
        gameManager.pauseAssistant(true);       //pausing assistant
        int action = clientConnection.getNextAction();
        switch (action){
            case FinalActions.RESUME_GAME:      //resume game, resume assistant
                gameManager.pauseAssistant(false);
                break;
            case FinalActions.TOGGLE_ASSIST:        //assistant on/off
                gameManager.toggleAssistant();
                break;
            case FinalActions.NEW_GAME:             //start new game
                gameManager.interruptAssistant();
                gameManager = new GameManager(clientConnection);
                gameManager.startOver();
                break;
            case FinalActions.SAVE_PROGRESS:
                gameManager.saveProgress();
                break;
            default:        //unexpected action
                clientConnection.crashConnection();
        }
    }
}
