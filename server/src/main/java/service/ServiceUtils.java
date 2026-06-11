package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;

public class ServiceUtils {
    public static void badRequestChecker(String message, Object... params) throws ResponseException {
        for (Object param : params) {
            if (param == null) {
                throw new ResponseException(400, message);
            }
        }
    }

    public static void validateAuth(String authToken, AuthDAO authDAO) throws ResponseException {
        ServiceUtils.badRequestChecker("Bad Request: Please register or login before logging out", authToken);

        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            int errorCode = e.getMessage().contains("Unauthorized") ? 401 : 500;
            throw new ResponseException(errorCode, e.getMessage());
        }
    }
}
