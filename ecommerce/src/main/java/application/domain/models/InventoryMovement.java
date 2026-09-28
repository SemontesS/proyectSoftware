package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * InventoryMovement es un elemento interno del added Inventory.
 * Represents una operación que modifica o registra el comportamiento de
 * las existencias (Domain Model — NexusMarket, sección 11).
 */
public class InventoryMovement {

    private final InventoryMovementType type;
    private final int quantity;
    private final LocalDateTime date;

    public InventoryMovement(InventoryMovementType type, int quantity, LocalDateTime date) {
        this.type = Objects.requireNonNull(type, "type es obligatorio");
        if (quantity <= 0) {
            throw new IllegalArgumentException("The quantity del movement debe ser mayor a zero.");
        }
        this.quantity = quantity;
        this.date = Objects.requireNonNullElseGet(date, LocalDateTime::now);
    }

    public InventoryMovementType getType() {
        return type;
    }

    public int getCantidad() {
        return quantity;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
