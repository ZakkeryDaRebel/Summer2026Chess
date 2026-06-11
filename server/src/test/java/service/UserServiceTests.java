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
        assertResponseException(badRequests,
                request -> this.userService.register(request), 400, "Bad Request");
    }

    @Test
    public void registerAlreadyTaken() {
        registerSuccessful();
        assertResponseException(new RegisterRequest[]{standardRegisterRequest},
                request -> this.userService.register(request), 403, "Already Taken");
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
        assertResponseException(badRequests, request -> this.userService.login(request),
                400, "Bad Request");
    }

    @Test
    public void loginUnauthorized() {
        LoginRequest[] badRequests = {
                new LoginRequest("not_bob", this.password),
                new LoginRequest(this.username, "9876")
        };
        assertResponseException(badRequests, request -> this.userService.login(request),
                401, "Unauthorized");
    }

    public interface Endpoint<T> {
        void sendRequest(T request) throws ResponseException;
    }

    public <T> void assertResponseException(T[] badRequests, Endpoint<T> endpoint, int errorCode, String errorMessage) {
        for (T request : badRequests) {
            ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                    endpoint.sendRequest(request));
            Assertions.assertEquals(errorCode, exception.getErrorCode());
            Assertions.assertTrue(exception.getMessage().contains(errorMessage));
        }
    }
}
