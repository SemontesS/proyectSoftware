package application.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Money represents un value monetario dentro del dominio. Prevents que
 * conceptos monetarios importantes sean manejados únicamente como números
 * sin significado de negocio.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 18.
 */
public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("El amount no puede ser nulo.");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("El amount no puede ser negativo.");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("The currency no puede ser vacía.");
        }
        currency = currency.trim().toUpperCase();
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    private void validateSameCurrency(Money other) {
        if (!Objects.equals(this.currency, other.currency)) {
            throw new IllegalArgumentException("No se pueden operar montos en monedas distintas.");
        }
    }
}
