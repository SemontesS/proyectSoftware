package com.ecommerce.domain.valueobjects;

/**
 * EstadoCarrito
 * -------------
 * Representa la situación del carrito durante el proceso de compra.
 * (Carrito.estado)
 *
 * NOTA: el documento no define un catálogo cerrado, pero sí distingue
 * un carrito disponible para seguir agregando productos de uno ya
 * utilizado para confirmar un pedido; esa distinción se refleja abajo.
 */
public enum EstadoCarrito implements DomainCatalog {

    ACTIVO("ACTIVO", "Activo",
            "El carrito está disponible y se le pueden seguir agregando productos."),
    CONVERTIDO("CONVERTIDO", "Convertido a pedido",
            "El carrito ya fue utilizado para confirmar un pedido y no puede modificarse.");

    private final String code;
    private final String name;
    private final String description;

    EstadoCarrito(String code, String name, String description) {
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
