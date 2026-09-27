package application.domain.valueobjects;

/**
 * Direccion representa la información necesaria para identificar un lugar
 * de entrega.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 17.
 */
public record Direccion(String tipo, String detalle, String ciudad) {

    public Direccion {
        if (detalle == null || detalle.isBlank()) {
            throw new IllegalArgumentException("El detalle de la dirección no puede ser vacío.");
        }
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("La ciudad de la dirección no puede ser vacía.");
        }
    }
}
