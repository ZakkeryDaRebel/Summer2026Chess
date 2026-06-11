package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.LoginRequest;
import request.RegisterRequest;
import response.AuthenticationResponse;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        return null;
    }

    public AuthenticationResponse login(LoginRequest request) {
        return null;
    }

    public void logout(String authToken) {
        return;
    }
}
