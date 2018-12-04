package project.serverside.user_related;

import project.serverside.FinalActions;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {


    private Map<Integer, User> map;

    public UserDatabase() {
        this.map = new HashMap<>(); // <hashcode, user>
    }

    int createUser(String username, String password) {   //creates new user
        synchronized (map) {
            if (map.containsKey(username.hashCode()))
                return FinalActions.USER_EXISTS;
            map.put(username.hashCode(), new User(password));
            return FinalActions.SUCCESSFULLY_CREATED;
        }
    }

    int userLogIn(String username, String password) {
        if (map.containsKey(username.hashCode()))                   //checks if user exists
            return map.get(username.hashCode()).logIn(password);    //if exists, checks password
        return FinalActions.USER_NOT_FOUND;
    }
}
