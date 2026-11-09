package application.domain.exceptions;

/** The requesting user may not perform the operation or access the resource (maps to HTTP 403). */
public class UnauthorizedOperationException extends DomainException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
