package project.serverside;

import project.serverside.user_related.UserDatabase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static final int PORT = 3000;

    public static void main(String[] args) {
        UserDatabase userDatabase = new UserDatabase();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true){
                Socket socket = serverSocket.accept();                //waiting for new connection
                new ClientThread(socket,userDatabase).start();        //starting new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}