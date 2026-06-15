package service;

import dataaccess.DAOFactory;
import dataaccess.MemoryDAOFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import request.LoginRequest;
import request.RegisterRequest;
import response.AuthenticationResponse;

public class UserServiceTests {

    private final String username = "bob";
    private final String password = "1234";
    private final LoginRequest standardLoginRequest = new LoginRequest(this.username, this.password);

    private UserService userService;

    @BeforeEach
    public void setUpServices() {
        DAOFactory daoFactory = new MemoryDAOFactory();
        this.userService = new UserService(daoFactory);
    }

    @Test
    public void registerSuccessful() {
        TestUtils.createAuthUser(this.userService);
    }

    @Test
    public void registerBadRequest() {
        String email = "bob@gmail.com";
        RegisterRequest[] badRequests = {
                new RegisterRequest(null, this.password, email),
                new RegisterRequest(this.username, null, email),
                new RegisterRequest(this.username, this.password, null),
                new RegisterRequest(null, null, email),
                new RegisterRequest(null, this.password, null),
                new RegisterRequest(this.username, null, null),
                new RegisterRequest(null, null, null)
        };
        TestUtils.assertBadRequest(badRequests, request -> this.userService.register(request));
    }

    @Test
    public void registerAlreadyTaken() {
        TestUtils.createAuthUser(this.userService);
        TestUtils.assertAlreadyTaken(new RegisterRequest[]{TestUtils.registerRequest},
                request -> this.userService.register(request));
    }

    @Test
    public void loginSuccessful() {
        TestUtils.createAuthUser(this.userService);
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
        String authToken = TestUtils.createAuthUser(this.userService);
        this.userService.logout(authToken);
    }

    @Test
    public void logoutUnauthorized() {
        TestUtils.assertUnauthorized(new String[]{"Fake authToken", null}, token -> this.userService.logout(token));
    }
}
