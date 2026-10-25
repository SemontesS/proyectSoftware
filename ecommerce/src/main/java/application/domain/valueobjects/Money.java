package application.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

/** Immutable monetary value. Operations are allowed only for the same currency. */
public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount, "Money amount is required.");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative.");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required.");
        }
        currency = currency.trim().toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money of(long amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money value is required.");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Money values with different currencies cannot be combined.");
        }
    }
}
