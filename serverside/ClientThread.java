package project.serverside;

import project.serverside.user_related.UserDatabase;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread implements ThreadCrashListener {
    private UserDatabase userDatabase;
    private Socket socket;
    private boolean crash = false;

    ClientThread(Socket socket, UserDatabase userDatabase) {
        this.socket = socket;
        this.userDatabase = userDatabase;
    }

    @Override
    public void run() {
        int action;             //closeable clientConnection ↓              on-crash listener ↓
        try (ClientConnection clientConnection = new ClientConnection(socket,(ThreadCrashListener)Thread.currentThread())) {
            ActionsManager actionsManager = new ActionsManager(clientConnection, userDatabase);
            actionsManager.onFirstLaunch();                       //attempting to login
            while (!crash) {                                      //reads next client action
                action = clientConnection.getNextAction();
                actionsManager.gotAction(action);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void crashThread() {         //on-crash listener
        crash = true;
    }
}
