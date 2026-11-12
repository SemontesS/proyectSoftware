package application.domain.valueobjects;

/** Non-negative quantity value. Context-specific rules may require a positive value. */
public record Quantity(int value) implements Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

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

    public Quantity plus(Quantity other) {
        return new Quantity(Math.addExact(value, other.value));
    }

    /** Subtracts the other quantity; fails if the result would be negative. */
    public Quantity minus(Quantity other) {
        return new Quantity(value - other.value);
    }

    public boolean isZero() {
        return value == 0;
    }

    @Override
    public int compareTo(Quantity other) {
        return Integer.compare(value, other.value);
    }
}
