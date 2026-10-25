package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InventoryMovement {
    @EqualsAndHashCode.Include
    private final Long movementId;
    private final InventoryMovementType type;
    private final int quantity;
    private final LocalDateTime date;

    public InventoryMovement(InventoryMovementType type, int quantity, LocalDateTime date) {
        this(null, type, quantity, date);
    }

    public InventoryMovement(Long movementId, InventoryMovementType type, int quantity, LocalDateTime date) {
        if (quantity <= 0) throw new IllegalArgumentException("Movement quantity must be greater than zero.");
        this.movementId = movementId;
        this.type = Objects.requireNonNull(type, "Movement type is required.");
        this.quantity = quantity;
        this.date = Objects.requireNonNullElseGet(date, LocalDateTime::now);
    }

    /** Legacy accessor retained because the existing model exposed this name. */
    public int getCantidad() {
        return quantity;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof InventoryMovement that)) return false;
        if (movementId == null || that.movementId == null) return this == other;
        return movementId.equals(that.movementId);
    }

    @Override
    public int hashCode() {
        return movementId == null ? System.identityHashCode(this) : movementId.hashCode();
    }
}
