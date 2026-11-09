package application.domain.valueobjects;

/** Stock keeping unit that identifies a product variant. */
public record Sku(String value) {
    public Sku {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SKU is required.");
        }
        value = value.trim().toUpperCase();
    }
}
