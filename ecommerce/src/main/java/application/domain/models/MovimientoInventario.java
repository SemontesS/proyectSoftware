package application.domain.models;

import application.domain.valueobjects.TipoMovimientoInventario;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MovimientoInventario es un elemento interno del agregado Inventario.
 * Representa una operación que modifica o registra el comportamiento de
 * las existencias (Domain Model — NexusMarket, sección 11).
 */
public class MovimientoInventario {

    private final TipoMovimientoInventario tipo;
    private final int cantidad;
    private final LocalDateTime fecha;

    public MovimientoInventario(TipoMovimientoInventario tipo, int cantidad, LocalDateTime fecha) {
        this.tipo = Objects.requireNonNull(tipo, "tipo es obligatorio");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero.");
        }
        this.cantidad = cantidad;
        this.fecha = Objects.requireNonNullElseGet(fecha, LocalDateTime::now);
    }

    public TipoMovimientoInventario getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
