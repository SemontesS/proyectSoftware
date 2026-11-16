package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

public record CartItemResponseDTO(Long productId, String sku, int quantity, MoneyDTO unitPrice,
        MoneyDTO subtotal) {}
