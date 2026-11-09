package application.domain.exceptions;

/** A uniqueness rule was violated (maps to HTTP 409). */
public class DuplicateEntityException extends DomainException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
