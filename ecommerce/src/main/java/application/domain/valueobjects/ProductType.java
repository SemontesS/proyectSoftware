package application.domain.valueobjects;

/**
 * ProductType diferencia los products según la forma en que se entregan.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 7.
 */
public enum ProductType {
    PHYSICAL("Físico", "Requires inventory y despacho."),
    DIGITAL("Digital", "Has entrega inmediata después del pago.");

    private final String name;
    private final String description;

    ProductType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescripcion() {
        return description;
    }
}
