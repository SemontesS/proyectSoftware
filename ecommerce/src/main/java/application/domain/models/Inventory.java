package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.Quantity;
import application.domain.valueobjects.Sku;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {
    @EqualsAndHashCode.Include
    private final Long inventoryId;
    private final Long productId;
    private final Sku sku;
    private final Long warehouseId;
    private Quantity availableQuantity;
    private Quantity reservedQuantity;
    private Quantity damagedQuantity;
    @Getter(AccessLevel.NONE)
    private final List<InventoryMovement> movements = new ArrayList<>();

    public Inventory(Long inventoryId, Long productId, Sku sku, Long warehouseId, Quantity availableQuantity) {
        this(inventoryId, productId, sku, warehouseId, availableQuantity, Quantity.ZERO, Quantity.ZERO);
    }

    public Inventory(Long inventoryId, Long productId, Sku sku, Long warehouseId,
                     Quantity availableQuantity, Quantity reservedQuantity, Quantity damagedQuantity) {
        this.inventoryId = Objects.requireNonNull(inventoryId, "Inventory ID is required.");
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        this.sku = Objects.requireNonNull(sku, "SKU is required.");
        this.warehouseId = Objects.requireNonNull(warehouseId, "Warehouse ID is required.");
        this.availableQuantity = Objects.requireNonNull(availableQuantity, "Available quantity is required.");
        this.reservedQuantity = Objects.requireNonNull(reservedQuantity, "Reserved quantity is required.");
        this.damagedQuantity = Objects.requireNonNull(damagedQuantity, "Damaged quantity is required.");
    }

    public void recordInbound(Quantity quantity) {
        requirePositive(quantity);
        availableQuantity = availableQuantity.plus(quantity);
        movements.add(new InventoryMovement(InventoryMovementType.INBOUND, quantity, LocalDateTime.now()));
    }

    public void recordReservation(Quantity quantity) {
        requirePositive(quantity);
        if (availableQuantity.compareTo(quantity) < 0) throw new BusinessRuleViolationException("Insufficient available inventory.");
        availableQuantity = availableQuantity.minus(quantity);
        reservedQuantity = reservedQuantity.plus(quantity);
        movements.add(new InventoryMovement(InventoryMovementType.RESERVATION, quantity, LocalDateTime.now()));
    }

    public void releaseReservation(Quantity quantity) {
        requirePositive(quantity);
        if (reservedQuantity.compareTo(quantity) < 0) throw new BusinessRuleViolationException("Insufficient reserved inventory.");
        reservedQuantity = reservedQuantity.minus(quantity);
        availableQuantity = availableQuantity.plus(quantity);
        movements.add(new InventoryMovement(InventoryMovementType.RELEASE, quantity, LocalDateTime.now()));
    }

    public void recordSaleOutbound(Quantity quantity) {
        requirePositive(quantity);
        if (reservedQuantity.compareTo(quantity) >= 0) reservedQuantity = reservedQuantity.minus(quantity);
        else if (availableQuantity.compareTo(quantity) >= 0) availableQuantity = availableQuantity.minus(quantity);
        else throw new BusinessRuleViolationException("Insufficient inventory for sale.");
        movements.add(new InventoryMovement(InventoryMovementType.SALE_OUTBOUND, quantity, LocalDateTime.now()));
    }

    public void recordReturn(Quantity quantity) {
        requirePositive(quantity);
        availableQuantity = availableQuantity.plus(quantity);
        movements.add(new InventoryMovement(InventoryMovementType.RETURN, quantity, LocalDateTime.now()));
    }

    public void markDamaged(Quantity quantity) {
        requirePositive(quantity);
        if (availableQuantity.compareTo(quantity) < 0) throw new BusinessRuleViolationException("Insufficient available inventory to mark as damaged.");
        availableQuantity = availableQuantity.minus(quantity);
        damagedQuantity = damagedQuantity.plus(quantity);
        movements.add(new InventoryMovement(InventoryMovementType.MARK_DAMAGED, quantity, LocalDateTime.now()));
    }

    /** Applies a signed correction (positive or negative) to the available quantity. */
    public void recordAdjustment(int delta) {
        if (delta == 0) throw new IllegalArgumentException("Adjustment delta cannot be zero.");
        int adjusted = availableQuantity.value() + delta;
        if (adjusted < 0) throw new BusinessRuleViolationException("Inventory cannot become negative.");
        availableQuantity = new Quantity(adjusted);
        movements.add(new InventoryMovement(InventoryMovementType.ADJUSTMENT,
                Quantity.positive(Math.abs(delta)), LocalDateTime.now()));
    }

    public List<InventoryMovement> getMovements() {
        return List.copyOf(movements);
    }

    private static void requirePositive(Quantity quantity) {
        Objects.requireNonNull(quantity, "Quantity is required.");
        if (quantity.isZero()) throw new IllegalArgumentException("Quantity must be greater than zero.");
    }

    /** Rebuilds a persisted inventory with its full movement history. For persistence adapters only. */
    public static Inventory restore(Long inventoryId, Long productId, Sku sku, Long warehouseId,
                                    Quantity availableQuantity, Quantity reservedQuantity, Quantity damagedQuantity,
                                    List<InventoryMovement> movements) {
        Inventory inventory = new Inventory(inventoryId, productId, sku, warehouseId,
                availableQuantity, reservedQuantity, damagedQuantity);
        inventory.movements.addAll(movements);
        return inventory;
    }
}
