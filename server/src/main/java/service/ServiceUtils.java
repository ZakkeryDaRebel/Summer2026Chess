package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;

public class ServiceUtils {
    public static void badRequestChecker(String message, Object... params) throws ResponseException {
        for (Object param : params) {
            if (param == null) {
                throw new ResponseException(400, message);
            }
        }
    }

    public static AuthData validateAuth(String authToken, AuthDAO authDAO) throws ResponseException {
        try {
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                throw new DataAccessException("Unauthorized");
            }
            return auth;
        } catch (DataAccessException e) {
            int errorCode = e.getMessage().contains("Unauthorized") ? 401 : 500;
            throw new ResponseException(errorCode, e.getMessage());
        }
    }
}
