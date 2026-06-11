package service;

import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import response.AuthenticationResponse;

import java.util.UUID;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(DAOFactory daoFactory) {
        this.authDAO = daoFactory.getAuthDAO();
        this.userDAO = daoFactory.getUserDAO();
    }

    public AuthenticationResponse register(RegisterRequest request) throws ResponseException {
        ServiceUtils.badRequestChecker("Bad Request: Please include the username, password, and email",
                request.username(), request.password(), request.email());

        try {
            this.userDAO.getUser(request.username());
            throw new ResponseException(403, "Already Taken: Please try a different username");
        } catch (DataAccessException _) {}

        UserData user = new UserData(request.username(), request.password(), request.email());
        try {
            this.userDAO.createUser(user);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Server error: Failed to create user. " + e.getMessage());
        }

        return createAuth(request.username());
    }

    public AuthenticationResponse login(LoginRequest request) throws ResponseException {
        ServiceUtils.badRequestChecker("Bad Request: Please include the username and password",
                request.username(), request.password());

        UserData user;
        try {
            user = this.userDAO.getUser(request.username());
            comparePasswords(request.password(), user.password());
        } catch (DataAccessException e) {
            int errorCode = e.getMessage().contains("Unauthorized") ? 401 : 500;
            throw new ResponseException(errorCode, e.getMessage());
        }

        return createAuth(request.username());
    }

    public void logout(String authToken) {
        ServiceUtils.badRequestChecker("Bad Request: Please register or login before logging out", authToken);

        try {
            this.authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            int errorCode = e.getMessage().contains("Unauthorized") ? 401 : 500;
            throw new ResponseException(errorCode, e.getMessage());
        }

        try {
            this.authDAO.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            throw new ResponseException(500, "Server error: Could not log you out. " + ex.getMessage());
        }
    }

    public AuthenticationResponse createAuth(String username) {
        AuthData auth = new AuthData(generateAuthToken(), username);
        try {
            this.authDAO.createAuth(auth);
            return new AuthenticationResponse(auth.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Server error: Failed to create authorization. " + e.getMessage());
        }
    }

    public void comparePasswords(String userPassword, String databasePassword) throws DataAccessException {
        if (!userPassword.equals(databasePassword)) {
            throw new DataAccessException("Unauthorized: Please enter a valid username and password");
        }
    }

    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
