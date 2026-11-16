package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

public record WarehouseResponseDTO(Long warehouseId, Long sellerId, String type, String status,
        AddressDTO location) {}
