package exception;

public class ResponseException extends RuntimeException {
    private final int errorCode;
    public ResponseException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
