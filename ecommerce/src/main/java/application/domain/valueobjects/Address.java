package application.domain.valueobjects;

/**
 * Address represents la información necesaria para identificar un lugar
 * de entrega.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 17.
 */
public record Address(String type, String detalle, String ciudad) {

    public Address {
        if (detalle == null || detalle.isBlank()) {
            throw new IllegalArgumentException("El detalle de la address no puede ser vacío.");
        }
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("The ciudad de la address no puede ser vacía.");
        }
    }
}
