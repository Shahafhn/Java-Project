package project.clientside;

import java.io.*;

class ImportExport {
    private ServerConnection serverConnection;
    private String path;


    ImportExport(ServerConnection serverConnection, String username){
        this.serverConnection = serverConnection;
        this.path = "C:\\Users\\Public\\Documents\\ClickerProject\\SaveFiles\\" + username + ".bin";
    }

    void initiateSaveFile(){    //looks for an existing save file for this username
        int read;
        File saveFile = new File(path);
        if (!saveFile.isFile()){    //if no file found, asks to start over
            noFileFound();
        } else if (saveFile.length() != 80){    //if file is not 80 bytes long, asks to start over
            illegalFile();
        } else {
            serverConnection.sendBytes(FinalActions.CONTINUE);       //lets the server know save file found
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(path);            //reads the file
                byte[] buffer = new byte[(int)saveFile.length()];
                read = inputStream.read(buffer);
                if(read == -1)
                    noFileFound(true);
                serverConnection.sendBytes(buffer);                //sends the save file to the server
            } catch (IOException e) {
                noFileFound(true);
                initiateSaveFile();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void noFileFound(){
        //invalid save file: false
        noFileFound(false);
    }

    private void noFileFound(boolean invalid){
        if (!invalid)
            System.out.println("Save file not found!");
        System.out.println("1. Start over.");
        System.out.println("2. Import save file.");
        System.out.println("9. Exit.");
        int selection = FirstRun.getNextUserSelection();
        switch (selection){
            case 1:         //new game
                serverConnection.sendBytes(FinalActions.START_OVER);
                break;
            case 2:         //user can now put his save file in this location ↓
                System.out.println("Please put your save file at:\nC:\\Users\\Public\\Documents\\ClickerProject\\SaveFiles\\");
                System.out.println("(your save file MUST be named after your username)");
                System.out.println("Press any key to continue, after you have put your save file at the destination folder:");
                FirstRun.getNextUserSelection();        //on user selection attempts to read the file
                initiateSaveFile();
                break;
            case 9:         //exiting game
                System.out.println("Exiting.");
                serverConnection.sendBytes(FinalActions.CRASH_GAME);        //letting server know to close connection
                serverConnection.crashConnection();
                break;
            default:
                System.out.println("Invalid selection.");
                noFileFound(true);
        }
    }

    void illegalFile(){
        System.out.println("The Save file located at:");
        System.out.println(path);
        System.out.println("is not supported.");
        noFileFound(true);
    }

    void saveProgress(){        //letting user know there is an attempt to save progress
        System.out.println("Attempting to save your progress.");
        serverConnection.saveProgress(path);
        int action = serverConnection.getNextAction();
        if (action == FinalActions.SAVED_SUCCESSFULLY)
            System.out.println("Progress successfully saved to:\n" + path);
        else
            System.out.println("Failed to save.");
        System.out.println("Exiting");
    }
}