package application.domain.valueobjects;

/**
 * EstadoReembolso representa la situación de un reembolso asociado a una
 * devolución procesada.
 *
 * La especificación incluye reembolsos, pero no define un catálogo
 * detallado de estados; los valores concretos quedan pendientes de
 * definición funcional.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 16.
 */
public record EstadoReembolso(String codigo) {

    public EstadoReembolso {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El estado del reembolso no puede ser vacío.");
        }
        codigo = codigo.trim().toUpperCase();
    }
}
