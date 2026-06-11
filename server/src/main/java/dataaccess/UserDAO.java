package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
