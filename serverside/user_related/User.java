package project.serverside.user_related;

import project.serverside.FinalActions;

class User{
    private String password;

    User(String password) {
        this.password = password;
    }

    int logIn(String password){
        if (this.password.equals(password))
            return FinalActions.SUCCESSFUL_LOGIN;
        return FinalActions.WRONG_PASS;
    }
}
