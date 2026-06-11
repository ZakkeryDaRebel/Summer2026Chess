package service;

import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import response.AuthenticationResponse;

public class TestUtils {
    public static void assertAuthenticationResponse(AuthenticationResponse response, String username) {
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.username(), username);
        Assertions.assertNotNull(response.authToken());
    }

    public interface Endpoint<T> {
        void sendRequest(T request) throws ResponseException;
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
