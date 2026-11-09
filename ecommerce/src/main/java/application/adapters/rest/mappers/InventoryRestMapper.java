package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.InventoryAllocationResponseDTO;
import application.adapters.rest.dtos.responses.InventoryResponseDTO;
import application.adapters.rest.dtos.responses.MovementResponseDTO;
import application.domain.models.Inventory;
import application.domain.models.InventoryAllocation;
import application.domain.models.InventoryMovement;

import java.util.List;

public final class InventoryRestMapper {

    private InventoryRestMapper() {
    }

    public static InventoryResponseDTO toDTO(Inventory inventory) {
        return new InventoryResponseDTO(inventory.getInventoryId(), inventory.getProductId(),
                inventory.getSku().value(), inventory.getWarehouseId(), inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(), inventory.getDamagedQuantity(),
                inventory.getMovements().stream().map(InventoryRestMapper::toMovementDTO).toList());
    }

    public static List<InventoryResponseDTO> toDTOs(List<Inventory> inventories) {
        return inventories.stream().map(InventoryRestMapper::toDTO).toList();
    }

    public static MovementResponseDTO toMovementDTO(InventoryMovement movement) {
        return new MovementResponseDTO(movement.getMovementId(), movement.getType().name(),
                movement.getQuantity(), movement.getDate());
    }

    public static InventoryAllocationResponseDTO toAllocationDTO(InventoryAllocation allocation) {
        return new InventoryAllocationResponseDTO(allocation.inventory().getInventoryId(),
                allocation.warehouseId(), allocation.quantity());
    }

    public static List<InventoryAllocationResponseDTO> toAllocationDTOs(List<InventoryAllocation> allocations) {
        return allocations.stream().map(InventoryRestMapper::toAllocationDTO).toList();
    }
}
