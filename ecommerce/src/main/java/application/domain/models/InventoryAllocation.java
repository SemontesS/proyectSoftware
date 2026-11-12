package application.domain.models;

import application.domain.valueobjects.Quantity;

/** Part of a stock reservation taken from one inventory (that is, from one warehouse). */
public record InventoryAllocation(Inventory inventory, Quantity quantity) {
    public Long warehouseId() {
        return inventory.getWarehouseId();
    }
}
