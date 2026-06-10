package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    private HashMap<String, AuthData> authDatabase;

    public MemoryAuthDAO() {
        this.authDatabase = new HashMap<>();
    }
}
