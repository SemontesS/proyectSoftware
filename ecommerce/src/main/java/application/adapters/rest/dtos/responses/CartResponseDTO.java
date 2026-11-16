package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.util.List;

public record CartResponseDTO(Long cartId, Long buyerId, String status, String currency,
        List<CartItemResponseDTO> items, MoneyDTO total) {}
