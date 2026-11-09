package application.domain.exceptions;

/** Wrong credentials or an invalid/expired token (maps to HTTP 401). */
public class InvalidCredentialsException extends UnauthorizedOperationException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
