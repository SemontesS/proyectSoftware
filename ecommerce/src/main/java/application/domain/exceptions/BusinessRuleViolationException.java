package application.domain.exceptions;

/** A business rule or state transition was violated (maps to HTTP 409/422). */
public class BusinessRuleViolationException extends DomainException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
