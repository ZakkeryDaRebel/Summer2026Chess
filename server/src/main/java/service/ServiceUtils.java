package service;

import exception.ResponseException;

public class ServiceUtils {
    public static void badRequestChecker(String message, Object... params) throws ResponseException {
        for (Object param : params) {
            if (param == null) {
                throw new ResponseException(400, message);
            }
        }
    }
}
