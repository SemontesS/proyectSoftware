package application.domain.valueobjects;

/** Non-blank invoice number. */
public record InvoiceNumber(String value) {
    public InvoiceNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invoice number is required.");
        }
        value = value.trim();
    }
}
