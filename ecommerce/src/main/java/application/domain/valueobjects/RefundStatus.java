package application.domain.valueobjects;

/**
 * RefundStatus represents la situación de un refund associated a una
 * return procesada.
 *
 * The especificación incluye reembolsos, pero no define un catalog
 * detallado de estados; los valores concretos quedan pendientes de
 * definición funcional.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 16.
 */
public record RefundStatus(String code) {

    public RefundStatus {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El status del refund no puede ser vacío.");
        }
        code = code.trim().toUpperCase();
    }
}
