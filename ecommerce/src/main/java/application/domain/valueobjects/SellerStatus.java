package application.domain.valueobjects;

/**
 * SellerStatus represents la situación current del seller dentro de
 * NexusMarket.
 *
 * The especificación contempla el status del seller, pero no define un
 * catalog completo de valores permitidos.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 6.
 */
public record SellerStatus(String code) {

    public SellerStatus {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El status del seller no puede ser vacío.");
        }
        code = code.trim().toUpperCase();
    }
}
