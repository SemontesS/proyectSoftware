package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.util.List;

public record ProductResponseDTO(Long productId, Long sellerId, String name, String type,
        String status, MoneyDTO price, List<VariantDTO> variants) {}
