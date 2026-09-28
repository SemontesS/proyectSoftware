package application.domain.services;

import application.domain.models.Inventory;

import java.util.List;
import java.util.Objects;

/**
 * InventoryReservationService coordina la reservation de inventory cuando una
 * operación commercial necesita separar existencias disponibles.
 *
 * El inventory es distribuido: cada registro de Inventory está
 * vinculado a un product y a una warehouse específica, por lo que puede
 * existir más de un Inventory para un mismo product (uno por warehouse).
 *
 * Fuente: Domain Services — NexusMarket, sección 6.
 */
public class InventoryReservationService {

    /**
     * Reserves existencias de un product específico a partir de los
     * inventarios disponibles para ese product (uno por warehouse).
     *
     * The estrategia de selección es simple: recorre los inventarios en el
     * orden recibido y reservation del primero que tenga quantity suficiente.
     * No se permiten existencias negativas ni reserve inventory
     * inexistente (regla protegida por el propio added Inventory).
     *
     * @param productInventories inventarios existentes para el product solicitado
     * @param productId             product sobre el que se solicita la reservation
     * @param quantity               quantity requerida
     * @return el Inventory sobre el cual se ejecutó la reservation
     */
    public Inventory reserve(List<Inventory> productInventories, Long productId, int quantity) {
        Objects.requireNonNull(productId, "productId es obligatorio");
        if (quantity <= 0) {
            throw new IllegalArgumentException("The quantity a reserve debe ser mayor a zero.");
        }
        if (productInventories == null || productInventories.isEmpty()) {
            throw new IllegalStateException("No existe inventory para el product " + productId + ".");
        }

        // 2. Identificar el inventory correspondiente with quantity suficiente.
        Inventory inventory = productInventories.stream()
                .filter(inv -> productId.equals(inv.getProductId()))
                .filter(inv -> inv.getAvailableQuantity() >= quantity)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay inventory suficiente para el product " + productId + "."));

        // 3-5. Validate availability, execute la reservation y record el movement.
        //      Inventory.recordReservation ya valida y protege la invariante >= 0.
        inventory.recordReservation(quantity);
        return inventory;
    }
}
