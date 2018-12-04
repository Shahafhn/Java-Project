package project.clientside;

import java.util.Scanner;

public abstract class FirstRun {

    static String onLaunch(ServerConnection serverConnection) {
        String username;
        int choice;
        System.out.println("1. Log in to existing account.");
        System.out.println("2. Create a new account.");
        System.out.println("9. Exit.");
        choice = getNextUserSelection();
        switch (choice) {
            case 1:         //existing user
                username = sendUserLogInDetails(false, serverConnection);
                break;
            case 2:         //new user
                username = sendUserLogInDetails(true, serverConnection);
                break;
            case 9:         //exiting game
                serverConnection.sendBytes(FinalActions.CRASH_GAME);
                serverConnection.crashConnection();
                return null;
            default:
                System.out.println("Invalid choice. Please try again:\n");
                return onLaunch(serverConnection);
        }
        return sentUserInfo(serverConnection,username);
    }

    private static String sentUserInfo(ServerConnection serverConnection,String username){
        switch (serverConnection.getNextAction()) {
            case FinalActions.SUCCESSFUL_LOGIN:
                System.out.println("Welcome back to the game " + username + "!");
                break;
            case FinalActions.SUCCESSFULLY_CREATED:
                System.out.println("Good luck " + username + "!");
                break;
            case FinalActions.WRONG_PASS:
                System.out.println("Wrong Password! Please try again.\n");
                return onLaunch(serverConnection);      //failed to login
            case FinalActions.USER_NOT_FOUND:
                System.out.println("Sorry, this user does not exist.\n");
                return onLaunch(serverConnection);      //failed to login
            case FinalActions.USER_EXISTS:
                System.out.println("Username '" + username + "' already exists.\nPlease choose another.\n");
                return onLaunch(serverConnection);      //failed to login
            default:        //unexpected action
                return null;
        }
        return username;        //if successful login - returns the username
    }

    private static String sendUserLogInDetails(boolean newUser, ServerConnection serverConnection) {
        String username = getNextUserString("Username", newUser, 10);       //reads username
        String password = getNextUserString("Password", newUser, 12);       //reads password
        serverConnection.sendBytes(newUser ? FinalActions.CREATE_USER : FinalActions.LOGIN);
        serverConnection.sendBytes(username);
        serverConnection.sendBytes(password);
        return username;
    }

    private static String getNextUserString(String type, boolean newUser, int max) {
        Scanner scan = new Scanner(System.in);
        String temp;
        while (true) {
            System.out.println(type + (newUser ? (" must be between 4 and " + max + " characters") : "") + ":");
            temp = scan.nextLine();
            if (temp.length() > max || temp.length() < 4) {
                System.out.println("Invalid " + type + ".");
                newUser = true;
                continue;
            }
            return temp;
        }
    }

    public static int getNextUserSelection() {
        Scanner scan = new Scanner(System.in);
        String temp;
        int selection;
        temp = scan.nextLine();
        try {
            selection = Integer.valueOf(temp);
        } catch (NumberFormatException e) {
            return FinalActions.CLICK;
        }
        return selection;
    }
}
