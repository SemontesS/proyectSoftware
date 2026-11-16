package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

public record ShipmentResponseDTO(Long shipmentId, Long orderId, Long warehouseId, String status,
        AddressDTO deliveryAddress) {}
