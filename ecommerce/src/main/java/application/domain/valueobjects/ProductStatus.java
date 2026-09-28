package application.domain.valueobjects;

/**
 * ProductStatus represents la situación del product dentro del catalog.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 8.
 */
public enum ProductStatus {
    PUBLISHED("Product disponible en el catalog."),
    SUSPENDED("Product temporalmente suspendido."),
    DISCONTINUED("Product que dejó de estar disponible.");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescripcion() {
        return description;
    }
}
