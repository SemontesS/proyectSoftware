package application.adapters.rest.dtos.responses;


public record InventoryAllocationResponseDTO(Long inventoryId, Long warehouseId, int quantity) {}
