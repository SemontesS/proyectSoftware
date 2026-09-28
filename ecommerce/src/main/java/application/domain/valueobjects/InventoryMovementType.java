package application.domain.valueobjects;

/**
 * InventoryMovementType represents el type de operación realizada sobre
 * las existencias. Is diferente de un status porque represents una
 * operación (movement) y no una situación current.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 11.
 */
public enum InventoryMovementType {
    INBOUND("Entrada de existencias."),
    RESERVATION("Separation de existencias."),
    SALIDA_VENTA("Disminución por una venta."),
    ADJUSTMENT("Correction de existencias."),
    RETURN("Reingreso de existencias.");

    private final String description;

    InventoryMovementType(String description) {
        this.description = description;
    }

    public String getDescripcion() {
        return description;
    }
}
