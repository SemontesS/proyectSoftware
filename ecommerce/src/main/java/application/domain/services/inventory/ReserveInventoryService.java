package application.domain.services.inventory;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.Inventory;
import application.domain.models.InventoryAllocation;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.Sku;
import application.domain.valueobjects.WarehouseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sets aside available stock for a commercial operation. Stock is distributed, so one request
 * can be split across several active warehouses (largest availability first).
 *
 * <p>{@link #plan} has no side effects, which lets a caller validate several reservations
 * before applying any of them with {@link #apply}.
 */
@Service
@RequiredArgsConstructor
public class ReserveInventoryService {
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    /** Reserves stock on behalf of an authorized operator. */
    public List<InventoryAllocation> reserve(User requestingUser, Long productId, Sku sku, int quantity) {
        permissions.validate(requestingUser, Permission.INVENTORY_RESERVE);
        List<InventoryAllocation> allocations = plan(productId, sku, quantity);
        apply(allocations);
        return allocations;
    }

    /** Computes how the quantity would be taken from the active warehouses. Modifies nothing. */
    public List<InventoryAllocation> plan(Long productId, Sku sku, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to reserve must be greater than zero.");
        List<Inventory> candidates = inventoryRepositoryPort.findByProductAndSku(productId, sku).stream()
                .filter(inventory -> inventory.getAvailableQuantity() > 0)
                .filter(inventory -> warehouseRepositoryPort.findById(inventory.getWarehouseId())
                        .map(warehouse -> warehouse.getStatus() == WarehouseStatus.ACTIVE)
                        .orElse(false))
                .sorted(Comparator.comparingInt(Inventory::getAvailableQuantity).reversed())
                .toList();

        List<InventoryAllocation> allocations = new ArrayList<>();
        int remaining = quantity;
        for (Inventory inventory : candidates) {
            if (remaining == 0) break;
            int taken = Math.min(remaining, inventory.getAvailableQuantity());
            allocations.add(new InventoryAllocation(inventory, taken));
            remaining -= taken;
        }
        if (remaining > 0) {
            throw new BusinessRuleViolationException(
                    "Insufficient inventory for product " + productId + " (" + sku.value() + ").");
        }
        return allocations;
    }

    /** Applies and persists a previously planned reservation. */
    public void apply(List<InventoryAllocation> allocations) {
        for (InventoryAllocation allocation : allocations) {
            allocation.inventory().recordReservation(allocation.quantity());
            inventoryRepositoryPort.save(allocation.inventory());
        }
    }
}
