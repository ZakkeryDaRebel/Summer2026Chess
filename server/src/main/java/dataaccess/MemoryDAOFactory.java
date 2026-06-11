package dataaccess;

public class MemoryDAOFactory implements DAOFactory {

    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;
    private final MemoryUserDAO userDAO;

    public MemoryDAOFactory() {
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.userDAO = new MemoryUserDAO();
    }

    public AuthDAO getAuthDAO() {
        return this.authDAO;
    }

    public GameDAO getGameDAO() {
        return this.gameDAO;
    }

    public UserDAO getUserDAO() {
        return this.userDAO;
    }
}
