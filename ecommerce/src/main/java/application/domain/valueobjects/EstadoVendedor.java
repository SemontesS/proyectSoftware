package com.ecommerce.domain.valueobjects;

/**
 * EstadoVendedor
 * --------------
 * Representa la situación actual del vendedor dentro de Ecommerce,
 * controlando si se encuentra habilitado para operar. (Vendedor.estadoVendedor)
 *
 */
public enum EstadoVendedor implements DomainCatalog {

    ACTIVO("ACTIVO", "Activo",
            "El vendedor se encuentra habilitado para registrar y administrar productos."),
    SUSPENDIDO("SUSPENDIDO", "Suspendido",
            "El vendedor tiene restringida temporalmente su operación."),
    INACTIVO("INACTIVO", "Inactivo",
            "El vendedor no se encuentra habilitado dentro de la plataforma.");

    private final String code;
    private final String name;
    private final String description;

    EstadoVendedor(String code, String name, String description) {
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
