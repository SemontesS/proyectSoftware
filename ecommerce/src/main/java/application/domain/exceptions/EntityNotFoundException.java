package application.domain.exceptions;

/** A required entity does not exist (maps to HTTP 404). */
public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entity, Object id) {
        super(entity + " not found: " + id + ".");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
