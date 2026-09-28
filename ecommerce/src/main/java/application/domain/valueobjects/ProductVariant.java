package application.domain.valueobjects;

/**
 * ProductVariant represents una attribute que diferencia una
 * presentación de un product (por ejemplo Color = Negro, Talla = M).
 *
 * The estructura definitiva podrá ajustarse si los requisitos detallan cómo
 * las variants afectan el inventory.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 9.
 */
public record ProductVariant(String attribute, String value) {

    public ProductVariant {
        if (attribute == null || attribute.isBlank()) {
            throw new IllegalArgumentException("El attribute de la variant no puede ser vacío.");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El value de la variant no puede ser vacío.");
        }
    }
}
