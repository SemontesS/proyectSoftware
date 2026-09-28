package application.domain.valueobjects;

/**
 * UserRole represents la responsibility que tiene un user dentro de
 * NexusMarket. Each user posee un único role.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 3.
 */
public enum UserRole {
    BUYER("Buyer", "Purchases products publicados."),
    SELLER("Seller", "Records y administra products."),
    ADMINISTRATOR("Administrador", "Manages vendedores y warehouses."),
    OPERADOR_LOGISTICO("Operador Logistics", "Manages la operación física y los despachos."),
    SUPERVISOR("Supervisor", "Performs consultas y seguimiento operativo.");

    private final String name;
    private final String responsibility;

    UserRole(String name, String responsibility) {
        this.name = name;
        this.responsibility = responsibility;
    }

    public String getName() {
        return name;
    }

    public String getResponsabilidad() {
        return responsibility;
    }
}
