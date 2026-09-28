package application.domain.valueobjects;

/**
 * ShipmentStatus represents la situación del proceso logístico associated a un
 * order (preparación, despacho, transporte y entrega).
 *
 * El catalog exacto de estados debe mantenerse alineado with las reglas
 * funcionales de logística cuando se definan.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 14.
 */
public record ShipmentStatus(String code) {

    public ShipmentStatus {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El status del shipment no puede ser vacío.");
        }
        code = code.trim().toUpperCase();
    }
}
