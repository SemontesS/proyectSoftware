package application.domain.valueobjects;

/** Non-negative quantity value. Context-specific rules may require a positive value. */
public record Quantity(int value) {
    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    public static Quantity positive(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        return new Quantity(value);
    }
}
