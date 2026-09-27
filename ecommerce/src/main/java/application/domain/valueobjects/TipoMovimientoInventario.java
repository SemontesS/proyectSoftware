package application.domain.valueobjects;

/**
 * TipoMovimientoInventario representa el tipo de operación realizada sobre
 * las existencias. Es diferente de un estado porque representa una
 * operación (movimiento) y no una situación actual.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 11.
 */
public enum TipoMovimientoInventario {
    INGRESO("Entrada de existencias."),
    RESERVA("Separación de existencias."),
    SALIDA_VENTA("Disminución por una venta."),
    AJUSTE("Corrección de existencias."),
    DEVOLUCION("Reingreso de existencias.");

    private final String descripcion;

    TipoMovimientoInventario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
