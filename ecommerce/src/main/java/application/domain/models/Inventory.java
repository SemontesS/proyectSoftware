package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
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
    private final String sku;
    private final Long warehouseId;
    private int availableQuantity;
    private int reservedQuantity;
    private int damagedQuantity;
    @Getter(AccessLevel.NONE)
    private final List<InventoryMovement> movements = new ArrayList<>();

    public Inventory(Long inventoryId, Long productId, Long warehouseId, int availableQuantity) {
        this(inventoryId, productId, "DEFAULT", warehouseId, availableQuantity, 0, 0);
    }

    public Inventory(Long inventoryId, Long productId, String sku, Long warehouseId,
                     int availableQuantity, int reservedQuantity, int damagedQuantity) {
        this.inventoryId = Objects.requireNonNull(inventoryId, "Inventory ID is required.");
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        this.sku = requireSku(sku);
        this.warehouseId = Objects.requireNonNull(warehouseId, "Warehouse ID is required.");
        validateNonNegative(availableQuantity, "Available quantity");
        validateNonNegative(reservedQuantity, "Reserved quantity");
        validateNonNegative(damagedQuantity, "Damaged quantity");
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.damagedQuantity = damagedQuantity;
    }

    public void recordInbound(int quantity) {
        requirePositive(quantity);
        availableQuantity += quantity;
        movements.add(new InventoryMovement(InventoryMovementType.INBOUND, quantity, LocalDateTime.now()));
    }

    public void recordReservation(int quantity) {
        requirePositive(quantity);
        if (availableQuantity < quantity) throw new IllegalStateException("Insufficient available inventory.");
        availableQuantity -= quantity;
        reservedQuantity += quantity;
        movements.add(new InventoryMovement(InventoryMovementType.RESERVATION, quantity, LocalDateTime.now()));
    }

    public void releaseReservation(int quantity) {
        requirePositive(quantity);
        if (reservedQuantity < quantity) throw new IllegalStateException("Insufficient reserved inventory.");
        reservedQuantity -= quantity;
        availableQuantity += quantity;
        movements.add(new InventoryMovement(InventoryMovementType.RELEASE, quantity, LocalDateTime.now()));
    }

    public void recordSaleOutbound(int quantity) {
        requirePositive(quantity);
        if (reservedQuantity >= quantity) reservedQuantity -= quantity;
        else if (availableQuantity >= quantity) availableQuantity -= quantity;
        else throw new IllegalStateException("Insufficient inventory for sale.");
        movements.add(new InventoryMovement(InventoryMovementType.SALE_OUTBOUND, quantity, LocalDateTime.now()));
    }

    public void recordReturn(int quantity) {
        requirePositive(quantity);
        availableQuantity += quantity;
        movements.add(new InventoryMovement(InventoryMovementType.RETURN, quantity, LocalDateTime.now()));
    }

    public void markDamaged(int quantity) {
        requirePositive(quantity);
        if (availableQuantity < quantity) throw new IllegalStateException("Insufficient available inventory to mark as damaged.");
        availableQuantity -= quantity;
        damagedQuantity += quantity;
        movements.add(new InventoryMovement(InventoryMovementType.MARK_DAMAGED, quantity, LocalDateTime.now()));
    }

    public void recordAdjustment(int quantity, int delta) {
        requirePositive(quantity);
        if (availableQuantity + delta < 0) throw new IllegalStateException("Inventory cannot become negative.");
        availableQuantity += delta;
        movements.add(new InventoryMovement(InventoryMovementType.ADJUSTMENT, quantity, LocalDateTime.now()));
    }

    public List<InventoryMovement> getMovements() {
        return List.copyOf(movements);
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
    }

    private static void validateNonNegative(int quantity, String label) {
        if (quantity < 0) throw new IllegalArgumentException(label + " cannot be negative.");
    }

    private static String requireSku(String sku) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required.");
        return sku.trim();
    }
}
