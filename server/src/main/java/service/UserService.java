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
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new ResponseException(400, "Bad Request: Please include the username, password, and email");
        }

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

        AuthData auth = new AuthData(generateAuthToken(), request.username());
        try {
            this.authDAO.createAuth(auth);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Server error: Failed to create authorization. " + e.getMessage());
        }
        return new AuthenticationResponse(auth.username(), auth.authToken());
    }

    public AuthenticationResponse login(LoginRequest request) {
        if (request.username() == null || request.password() == null) {
            throw new ResponseException(400, "Bad Request: Please include the username and password");
        }

        UserData user;
        try {
            user = this.userDAO.getUser(request.username());
            comparePasswords(request.password(), user.password());
        } catch (DataAccessException e) {
            int errorCode = e.getMessage().contains("Unauthorized") ? 401 : 500;
            throw new ResponseException(errorCode, e.getMessage());
        }
        return null;
    }

    public void logout(String authToken) {
        return;
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
