package application.domain.valueobjects;

/**
 * EstadoProducto representa la situación del producto dentro del catálogo.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 8.
 */
public enum EstadoProducto {
    PUBLICADO("Producto disponible en el catálogo."),
    SUSPENDIDO("Producto temporalmente suspendido."),
    DESCONTINUADO("Producto que dejó de estar disponible.");

    private final String descripcion;

    EstadoProducto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
