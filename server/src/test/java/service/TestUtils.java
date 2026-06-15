package service;

import dataaccess.DAOFactory;
import dataaccess.MemoryDAOFactory;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import request.RegisterRequest;
import response.AuthenticationResponse;

import java.util.stream.Stream;

public class TestUtils {

    public static Stream<DAOFactory> daoFactoryProvider() {
        return Stream.of(
                new MemoryDAOFactory()
                //new UserService(new SqlUserDAO())
        );
    }

    public static final RegisterRequest registerRequest = new RegisterRequest("bob", "1234", "bob@gmail.com");

    public static String createAuthUser(UserService userService) {
        AuthenticationResponse response = userService.register(registerRequest);
        TestUtils.assertAuthenticationResponse(response, registerRequest.username());
        return response.authToken();
    }

    public static void assertAuthenticationResponse(AuthenticationResponse response, String username) {
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.username(), username);
        Assertions.assertNotNull(response.authToken());
    }

    public interface Endpoint<T> {
        void sendRequest(T request) throws ResponseException;
    }

    public static <T> void assertBadRequest(T[] badRequests, Endpoint<T> endpoint) {
        assertResponseException(badRequests, endpoint, 400, "Bad Request");
    }

    public static <T> void assertUnauthorized(T[] badRequests, Endpoint<T> endpoint) {
        assertResponseException(badRequests, endpoint, 401, "Unauthorized");
    }

    public static <T> void assertAlreadyTaken(T[] badRequests, Endpoint<T> endpoint) {
        assertResponseException(badRequests, endpoint, 403, "Already Taken");
    }

    public static <T> void assertResponseException(T[] badRequests, Endpoint<T> endpoint, int errorCode, String errorMessage) {
        for (T request : badRequests) {
            ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                    endpoint.sendRequest(request));
            Assertions.assertEquals(errorCode, exception.getErrorCode());
            Assertions.assertTrue(exception.getMessage().contains(errorMessage));
        }
    }
}
