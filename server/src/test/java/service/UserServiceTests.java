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
        AuthenticationResponse response = this.userService.register(this.standardRegisterRequest);
        assertAuthenticationResponse(response);
    }

    public void assertAuthenticationResponse(AuthenticationResponse response) {
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.username(), this.username);
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest[] badRequests = {null,
                new RegisterRequest(null, this.password, this.email),
                new RegisterRequest(this.username, null, this.email),
                new RegisterRequest(this.username, this.password, null),
                new RegisterRequest(null, null, this.email),
                new RegisterRequest(null, this.password, null),
                new RegisterRequest(this.username, null, null),
                new RegisterRequest(null, null, null)
        };
        for (RegisterRequest request : badRequests) {
            ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                    this.userService.register(request));
            Assertions.assertEquals(400, exception.getErrorCode());
            Assertions.assertTrue(exception.getMessage().contains("Bad Request"));
        }
    }

    @Test
    public void registerAlreadyTaken() {
        registerSuccessful();
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                this.userService.register(this.standardRegisterRequest));
        Assertions.assertEquals(403, exception.getErrorCode());
        Assertions.assertTrue(exception.getMessage().contains("Already Taken"));
    }

    @Test
    public void loginSuccessful() {
        registerSuccessful();
        AuthenticationResponse response = this.userService.login(this.standardLoginRequest);
        assertAuthenticationResponse(response);
    }

    @Test
    public void loginBadRequest() {
        LoginRequest[] badRequests = {null,
                new LoginRequest(null, this.password),
                new LoginRequest(this.username, null),
                new LoginRequest(null, null)
        };
        for (LoginRequest request : badRequests) {
            ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                    this.userService.login(request));
            Assertions.assertEquals(400, exception.getErrorCode());
            Assertions.assertTrue(exception.getMessage().contains("Bad Request"));
        }
    }
}
