package application.domain.valueobjects;

/** Immutable product variant descriptor. SKU identifies the variant within its product. */
public record ProductVariant(String sku, String attribute, String value) {
    public ProductVariant {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Variant SKU is required.");
        }
        if (attribute == null || attribute.isBlank()) {
            throw new IllegalArgumentException("Variant attribute is required.");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variant value is required.");
        }
        sku = sku.trim();
        attribute = attribute.trim();
        value = value.trim();
    }
}
