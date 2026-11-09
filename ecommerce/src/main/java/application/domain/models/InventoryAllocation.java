package application.domain.models;

/** Part of a stock reservation taken from one inventory (that is, from one warehouse). */
public record InventoryAllocation(Inventory inventory, int quantity) {
    public Long warehouseId() {
        return inventory.getWarehouseId();
    }
}
