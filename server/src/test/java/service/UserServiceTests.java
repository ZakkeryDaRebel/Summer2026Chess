package service;

import dataaccess.DAOFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import response.AuthenticationResponse;

public abstract class UserServiceTests {

    private final String username = "bob";
    private final String password = "1234";
    private final String email = "bob@gmail.com";
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
        TestUtils.createAuthUser(TestUtils.registerRequest, this.userService);
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
        TestUtils.createAuthUser(TestUtils.registerRequest, this.userService);
        TestUtils.assertAlreadyTaken(new RegisterRequest[]{TestUtils.registerRequest},
                request -> this.userService.register(request));
    }

    @Test
    public void loginSuccessful() {
        TestUtils.createAuthUser(TestUtils.registerRequest, this.userService);
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
        String authToken = TestUtils.createAuthUser(TestUtils.registerRequest, this.userService);
        this.userService.logout(authToken);
    }

    @Test
    public void logoutBadRequest() {
        TestUtils.assertBadRequest(new String[]{null}, token -> this.userService.logout(token));
    }

    @Test
    public void logoutUnauthorized() {
        TestUtils.assertUnauthorized(new String[]{"Fake authToken"}, token -> this.userService.logout(token));
    }
}
