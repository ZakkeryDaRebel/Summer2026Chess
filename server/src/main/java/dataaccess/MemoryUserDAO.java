package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO {

    private HashMap<String, UserData> userDatabase;

    public MemoryUserDAO() {
        this.userDatabase = new HashMap<>();
    }
}
