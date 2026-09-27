package application.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Dinero representa un valor monetario dentro del dominio. Evita que
 * conceptos monetarios importantes sean manejados únicamente como números
 * sin significado de negocio.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 18.
 */
public record Dinero(BigDecimal monto, String moneda) {

    public Dinero {
        if (monto == null) {
            throw new IllegalArgumentException("El monto no puede ser nulo.");
        }
        if (monto.signum() < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new IllegalArgumentException("La moneda no puede ser vacía.");
        }
        moneda = moneda.trim().toUpperCase();
    }

    public static Dinero of(double monto, String moneda) {
        return new Dinero(BigDecimal.valueOf(monto), moneda);
    }

    public Dinero sumar(Dinero otro) {
        validarMismaMoneda(otro);
        return new Dinero(this.monto.add(otro.monto), this.moneda);
    }

    public Dinero multiplicar(int cantidad) {
        return new Dinero(this.monto.multiply(BigDecimal.valueOf(cantidad)), this.moneda);
    }

    private void validarMismaMoneda(Dinero otro) {
        if (!Objects.equals(this.moneda, otro.moneda)) {
            throw new IllegalArgumentException("No se pueden operar montos en monedas distintas.");
        }
    }
}
