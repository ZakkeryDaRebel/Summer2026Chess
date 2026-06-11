package dataaccess;

public interface DAOFactory {
    AuthDAO getAuthDAO();
    GameDAO getGameDAO();
    UserDAO getUserDAO();
}
