package project.clientside;

import java.io.IOException;
import java.net.Socket;

public class Main {

    private static final String HOST = "192.168.14.175";
    private static final int PORT = 3000;
    private static boolean crash = false;

    public static void main(String[] args){
        Socket socket;
        int action;
        //closeable serverConnection
        try (ServerConnection serverConnection = new ServerConnection(getThreadCrasher())) {
            //Connects to the server
            socket = new Socket(HOST,PORT);
            serverConnection.setConnection(socket);
            ActionsManager actionsManager = new ActionsManager(serverConnection);
            //Log-in screen and save file upload
            actionsManager.onFirstLaunch();
            while (!crash){     //reads the next action from server
                action = serverConnection.getNextAction();
                actionsManager.gotAction(action);
            }
        } catch (IOException e) {
            System.out.println("Could not connect to the server. Exiting.");
        }
    }
    private static ThreadCrashListener getThreadCrasher(){      //generates on-click interface
        return new ThreadCrashListener() {
            @Override
            public void crashThread() {
                crash = true;
            }
        };
    }
}