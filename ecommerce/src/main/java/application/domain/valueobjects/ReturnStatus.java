package application.domain.valueobjects;

/**
 * ReturnStatus represents la situación de una return associated a un
 * order, desde la solicitud hasta su finalización.
 *
 * The especificación contempla devoluciones pero no entrega un catalog
 * detallado de estados.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 15.
 */
public record ReturnStatus(String code) {

    public ReturnStatus {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El status de la return no puede ser vacío.");
        }
        code = code.trim().toUpperCase();
    }
}
