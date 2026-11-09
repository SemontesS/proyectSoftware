package application.domain.valueobjects;

import java.util.Objects;

/** Immutable product variant descriptor. SKU identifies the variant within its product. */
public record ProductVariant(Sku sku, String attribute, String value) {
    public ProductVariant {
        Objects.requireNonNull(sku, "Variant SKU is required.");
        if (attribute == null || attribute.isBlank()) {
            throw new IllegalArgumentException("Variant attribute is required.");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variant value is required.");
        }
        attribute = attribute.trim();
        value = value.trim();
    }
}
