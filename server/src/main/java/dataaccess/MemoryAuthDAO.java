package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    private HashMap<String, AuthData> authDatabase;

    public MemoryAuthDAO() {
        this.authDatabase = new HashMap<>();
    }

    public void createAuth(AuthData auth) {
        this.authDatabase.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = this.authDatabase.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
        return auth;
    }
}
