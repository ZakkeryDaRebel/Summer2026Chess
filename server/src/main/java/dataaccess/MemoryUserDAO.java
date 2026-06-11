package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private HashMap<String, UserData> userDatabase;

    public MemoryUserDAO() {
        this.userDatabase = new HashMap<>();
    }

    public void createUser(UserData user) {
        this.userDatabase.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = this.userDatabase.get(username);
        if (user == null) {
            throw new DataAccessException("Unauthorized");
        }
        return user;
    }

    public void clearUsers() throws DataAccessException {
        this.userDatabase = new HashMap<>();
    }
}
