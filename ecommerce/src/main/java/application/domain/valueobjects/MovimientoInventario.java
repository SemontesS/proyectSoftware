package com.ecommerce.domain.valueobjects;

/**
 * MovimientoInventario
 * ---------------------
 * Representa los movimientos que puede sufrir el inventario de un
 * producto en una bodega.
 */
public enum MovimientoInventario implements DomainCatalog {

    INGRESO("INGRESO", "Ingreso",

            "Entrada de existencias al inventario."),
    RESERVA("RESERVA", "Reserva",
            "Separación de existencias para una operación."),
    SALIDA_VENTA("SALIDA_VENTA", "Salida por venta",
            "Descuento de existencias debido a una venta."),
    AJUSTE("AJUSTE", "Ajuste",
            "Modificación de la cantidad registrada por una corrección."),
    DEVOLUCION("DEVOLUCION", "Devolución",
            "Reingreso relacionado con una devolución.");

    private final String code;
    private final String name;
    private final String description;

    MovimientoInventario(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
