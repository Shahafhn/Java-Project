package project.serverside.user_related;

import project.serverside.FinalActions;
import project.serverside.ClientConnection;

public class UserManager {
    private UserDatabase userDatabase;
    private String username;
    private ClientConnection clientConnection;

    public UserManager(UserDatabase userDatabase, ClientConnection clientConnection) {
        this.userDatabase = userDatabase;
        this.clientConnection = clientConnection;
    }

    public int userSignIn(int choice){
        int attempt;
        String password;
        username = clientConnection.getNextString(4, 10);
        password = clientConnection.getNextString(4, 12);
        if(choice == FinalActions.CREATE_USER) //successfully created, user exists,
            attempt = userDatabase.createUser(username,password);
        else                  //successful login, wrong password, user not found.
            attempt = userDatabase.userLogIn(username,password);
        clientConnection.sendBytes(attempt);
        return attempt;
    }
}
