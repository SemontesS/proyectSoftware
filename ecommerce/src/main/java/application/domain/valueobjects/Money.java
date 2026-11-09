package application.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

/** Immutable monetary value. Operations are allowed only for the same currency. */
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Money amount is required.");
        Objects.requireNonNull(currency, "Currency is required.");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative.");
        }
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(long amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
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
        if (currency != other.currency) {
            throw new IllegalArgumentException("Money values with different currencies cannot be combined.");
        }
    }
}
