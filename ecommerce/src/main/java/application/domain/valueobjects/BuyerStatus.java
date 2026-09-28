package application.domain.valueobjects;

/**
 * BuyerStatus represents la situación commercial del buyer.
 *
 * The especificación funcional establece que este attribute es obligatorio,
 * pero no entrega un catalog detallado de valores permitidos. For ello no
 * se inventan valores additional; se modela como un código controlado.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 5.
 */
public record BuyerStatus(String code) {

    public BuyerStatus {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El status del buyer no puede ser vacío.");
        }
        code = code.trim().toUpperCase();
    }
}
