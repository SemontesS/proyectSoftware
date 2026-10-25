package application.domain.valueobjects;

/** A movement describes an inventory operation, not an inventory state. */
public enum InventoryMovementType {
    INBOUND,
    RESERVATION,
    RELEASE,
    SALE_OUTBOUND,
    ADJUSTMENT,
    MARK_DAMAGED,
    RETURN
}
