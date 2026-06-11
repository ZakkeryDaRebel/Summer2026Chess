package service;

import dataaccess.DAOFactory;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import response.AuthenticationResponse;

public abstract class UserServiceTests {

    private final String username = "bob";
    private final String password = "1234";
    private final String email = "bob@gmail.com";
    private final RegisterRequest standardRegisterRequest = new RegisterRequest(this.username, this.password, this.email);
    private final LoginRequest standardLoginRequest = new LoginRequest(this.username, this.password);

    private UserService userService;

    @BeforeEach
    public void setUpServices() {
        DAOFactory daoFactory = createFactory();
        this.userService = new UserService(daoFactory);
    }

    protected abstract DAOFactory createFactory();

    @Test
    public void registerSuccessful() {
        TestUtils.createAuthUser(standardRegisterRequest, this.userService);
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest[] badRequests = {
                new RegisterRequest(null, this.password, this.email),
                new RegisterRequest(this.username, null, this.email),
                new RegisterRequest(this.username, this.password, null),
                new RegisterRequest(null, null, this.email),
                new RegisterRequest(null, this.password, null),
                new RegisterRequest(this.username, null, null),
                new RegisterRequest(null, null, null)
        };
        TestUtils.assertBadRequest(badRequests, request -> this.userService.register(request));
    }

    @Test
    public void registerAlreadyTaken() {
        TestUtils.createAuthUser(standardRegisterRequest, this.userService);
        TestUtils.assertAlreadyTaken(new RegisterRequest[]{standardRegisterRequest},
                request -> this.userService.register(request));
    }

    @Test
    public void loginSuccessful() {
        TestUtils.createAuthUser(standardRegisterRequest, this.userService);
        AuthenticationResponse response = this.userService.login(this.standardLoginRequest);
        TestUtils.assertAuthenticationResponse(response, this.username);
    }

    @Test
    public void loginBadRequest() {
        LoginRequest[] badRequests = {
                new LoginRequest(null, this.password),
                new LoginRequest(this.username, null),
                new LoginRequest(null, null)
        };
        TestUtils.assertBadRequest(badRequests, request -> this.userService.login(request));
    }

    @Test
    public void loginUnauthorized() {
        LoginRequest[] badRequests = {
                new LoginRequest("not_bob", this.password),
                new LoginRequest(this.username, "9876")
        };
        TestUtils.assertUnauthorized(badRequests, request -> this.userService.login(request));
    }

    @Test
    public void logoutSuccessful() {
        String authToken = TestUtils.createAuthUser(standardRegisterRequest, this.userService);
        this.userService.logout(authToken);
    }

    @Test
    public void logoutBadRequest() {
        TestUtils.assertBadRequest(new String[]{null}, request -> this.userService.logout(request));
    }
}
