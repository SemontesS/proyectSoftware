package application.domain.valueobjects;

/** Non-blank identity document number. Uniqueness is enforced by the user repository. */
public record IdentityDocument(String value) {
    public IdentityDocument {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Identity document is required.");
        }
        value = value.trim();
    }
}
