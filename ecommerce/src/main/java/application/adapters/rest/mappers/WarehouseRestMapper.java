package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.domain.models.Warehouse;

public final class WarehouseRestMapper {

    private WarehouseRestMapper() {
    }

    public static WarehouseResponseDTO toDTO(Warehouse warehouse) {
        return new WarehouseResponseDTO(warehouse.getWarehouseId(), warehouse.getSellerId(),
                warehouse.getType().name(), warehouse.getStatus().name(),
                CommonRestMapper.toDTO(warehouse.getLocation()));
    }
}
