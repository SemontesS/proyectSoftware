package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

public record OrderLineResponseDTO(Long productId, String sku, Long sellerId, Long warehouseId,
        int quantity, MoneyDTO unitPrice, MoneyDTO subtotal) {}
