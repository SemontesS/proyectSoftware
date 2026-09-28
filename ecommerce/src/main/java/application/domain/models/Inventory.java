package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventory es un Aggregate Root (Domain Model — NexusMarket, sección 20).
 * Represents las existencias de un product dentro de una warehouse
 * específica y protege la regla: availableQuantity >= 0
 * (no se permiten existencias negativas).
 */
public class Inventory {

    private final Long inventoryId;
    private final Long productId;
    private final Long warehouseId;
    private int availableQuantity;
    private final List<InventoryMovement> movements = new ArrayList<>();

    public Inventory(Long inventoryId, Long productId, Long warehouseId, int availableQuantity) {
        this.inventoryId = Objects.requireNonNull(inventoryId, "inventoryId es obligatorio");
        this.productId = Objects.requireNonNull(productId, "productId es obligatorio");
        this.warehouseId = Objects.requireNonNull(warehouseId, "warehouseId es obligatorio");
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("availableQuantity no puede ser negativa.");
        }
        this.availableQuantity = availableQuantity;
    }

    /** Records un inbound de existencias (increases availableQuantity). */
    public void recordInbound(int quantity) {
        applyMovement(InventoryMovementType.INBOUND, quantity, quantity);
    }

    /** Records una outbound por venta (disminuye availableQuantity). */
    public void recordSaleOutbound(int quantity) {
        applyMovement(InventoryMovementType.SALIDA_VENTA, quantity, -quantity);
    }

    /** Records una reservation de existencias (disminuye availableQuantity). */
    public void recordReservation(int quantity) {
        applyMovement(InventoryMovementType.RESERVATION, quantity, -quantity);
    }

    /** Records un re-entry por return (increases availableQuantity). */
    public void recordReturn(int quantity) {
        applyMovement(InventoryMovementType.RETURN, quantity, quantity);
    }

    /** Records un adjustment manual; delta puede ser positivo o negativo. */
    public void recordAdjustment(int absoluteQuantity, int delta) {
        applyMovement(InventoryMovementType.ADJUSTMENT, absoluteQuantity, delta);
    }

    private void applyMovement(InventoryMovementType type, int movementQuantity, int delta) {
        int nuevaCantidad = this.availableQuantity + delta;
        if (nuevaCantidad < 0) {
            throw new IllegalStateException("The operación dejaría existencias negativas, lo cual no está permitido.");
        }
        this.availableQuantity = nuevaCantidad;
        this.movements.add(new InventoryMovement(type, movementQuantity, LocalDateTime.now()));
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public List<InventoryMovement> getMovements() {
        return List.copyOf(movements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory that)) return false;
        return Objects.equals(inventoryId, that.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }
}
