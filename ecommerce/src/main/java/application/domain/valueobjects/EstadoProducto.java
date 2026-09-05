package com.ecommerce.domain.valueobjects;

/**
 * EstadoProducto
 * --------------
 * Representa la situación del producto dentro del catálogo. (Producto.estado)
 */
public enum EstadoProducto implements DomainCatalog {

    PUBLICADO("PUBLICADO", "Publicado",
            "Producto disponible dentro del catálogo."),
    SUSPENDIDO("SUSPENDIDO", "Suspendido",
            "Producto temporalmente suspendido."),
    DESCONTINUADO("DESCONTINUADO", "Descontinuado",
            "Producto que dejó de estar disponible dentro del catálogo.");

    private final String code;
    private final String name;
    private final String description;

    EstadoProducto(String code, String name, String description) {
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
