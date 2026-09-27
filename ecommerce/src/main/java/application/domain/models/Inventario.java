package application.domain.models;

import application.domain.valueobjects.TipoMovimientoInventario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventario es un Aggregate Root (Domain Model — NexusMarket, sección 20).
 * Representa las existencias de un producto dentro de una bodega
 * específica y protege la regla: cantidadDisponible >= 0
 * (no se permiten existencias negativas).
 */
public class Inventario {

    private final Long idInventario;
    private final Long idProducto;
    private final Long idBodega;
    private int cantidadDisponible;
    private final List<MovimientoInventario> movimientos = new ArrayList<>();

    public Inventario(Long idInventario, Long idProducto, Long idBodega, int cantidadDisponible) {
        this.idInventario = Objects.requireNonNull(idInventario, "idInventario es obligatorio");
        this.idProducto = Objects.requireNonNull(idProducto, "idProducto es obligatorio");
        this.idBodega = Objects.requireNonNull(idBodega, "idBodega es obligatorio");
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException("cantidadDisponible no puede ser negativa.");
        }
        this.cantidadDisponible = cantidadDisponible;
    }

    /** Registra un ingreso de existencias (aumenta cantidadDisponible). */
    public void registrarIngreso(int cantidad) {
        aplicarMovimiento(TipoMovimientoInventario.INGRESO, cantidad, cantidad);
    }

    /** Registra una salida por venta (disminuye cantidadDisponible). */
    public void registrarSalidaPorVenta(int cantidad) {
        aplicarMovimiento(TipoMovimientoInventario.SALIDA_VENTA, cantidad, -cantidad);
    }

    /** Registra una reserva de existencias (disminuye cantidadDisponible). */
    public void registrarReserva(int cantidad) {
        aplicarMovimiento(TipoMovimientoInventario.RESERVA, cantidad, -cantidad);
    }

    /** Registra un reingreso por devolución (aumenta cantidadDisponible). */
    public void registrarDevolucion(int cantidad) {
        aplicarMovimiento(TipoMovimientoInventario.DEVOLUCION, cantidad, cantidad);
    }

    /** Registra un ajuste manual; delta puede ser positivo o negativo. */
    public void registrarAjuste(int cantidadAbsoluta, int delta) {
        aplicarMovimiento(TipoMovimientoInventario.AJUSTE, cantidadAbsoluta, delta);
    }

    private void aplicarMovimiento(TipoMovimientoInventario tipo, int cantidadMovimiento, int delta) {
        int nuevaCantidad = this.cantidadDisponible + delta;
        if (nuevaCantidad < 0) {
            throw new IllegalStateException("La operación dejaría existencias negativas, lo cual no está permitido.");
        }
        this.cantidadDisponible = nuevaCantidad;
        this.movimientos.add(new MovimientoInventario(tipo, cantidadMovimiento, LocalDateTime.now()));
    }

    public Long getIdInventario() {
        return idInventario;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public Long getIdBodega() {
        return idBodega;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public List<MovimientoInventario> getMovimientos() {
        return List.copyOf(movimientos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventario that)) return false;
        return Objects.equals(idInventario, that.idInventario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInventario);
    }
}
