package application.domain.valueobjects;

/** Generic non-empty identifier value object. */
public record Identifier(String value) {
    public Identifier {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Identifier value is required.");
        }
        value = value.trim();
    }
}
