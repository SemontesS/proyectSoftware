package com.ecommerce.domain.valueobjects;

/**
 * EstadoInventario
 * ----------------
 * Representa la condición de las existencias asociadas a un producto
 * y una bodega. (Inventario.estadoExistencia)
   
 */
public enum EstadoInventario implements DomainCatalog {

    DISPONIBLE("DISPONIBLE", "Disponible",
            "Existen unidades libres para ser reservadas o vendidas."),
    RESERVADO("RESERVADO", "Reservado",
            "Existencias separadas temporalmente para una operación en curso."),
    AGOTADO("AGOTADO", "Agotado",
            "No hay existencias disponibles para el producto en la bodega.");

    private final String code;
    private final String name;
    private final String description;

    EstadoInventario(String code, String name, String description) {
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
