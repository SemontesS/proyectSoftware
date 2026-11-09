package application.domain.exceptions;

/** Base class of every business error raised by the domain layer. */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
