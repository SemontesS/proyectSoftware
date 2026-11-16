package application.adapters.rest.dtos.responses;


import java.util.List;

public record InventoryResponseDTO(Long inventoryId, Long productId, String sku, Long warehouseId,
        int availableQuantity, int reservedQuantity, int damagedQuantity,
        List<MovementResponseDTO> movements) {}
