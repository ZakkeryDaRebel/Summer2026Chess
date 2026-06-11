package service;

import dataaccess.DAOFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import response.AuthenticationResponse;

public abstract class UserServiceTests {

    private final String username = "bob";
    private final String password = "1234";
    private final String email = "bob@gmail.com";

    private UserService userService;

    @BeforeEach
    public void setUpServices() {
        DAOFactory daoFactory = createFactory();
        this.userService = new UserService(daoFactory);
    }

    protected abstract DAOFactory createFactory();

    @Test
    public void registerSuccessful() {
        RegisterRequest request = new RegisterRequest(this.username, this.password, this.email);
        AuthenticationResponse response = this.userService.register(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.username(), this.username);
        Assertions.assertNotNull(response.authToken());
    }
}
