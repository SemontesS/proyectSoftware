package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {
    }

    public static Warehouse toDomain(WarehouseJpaEntity entity) {
        return new Warehouse(entity.getId(), entity.getSellerId(), JpaCommonMapper.toDomain(entity.getLocation()),
                WarehouseType.valueOf(entity.getType()), WarehouseStatus.valueOf(entity.getStatus()));
    }

    public static void updateEntity(WarehouseJpaEntity entity, Warehouse warehouse) {
        entity.setId(warehouse.getWarehouseId());
        entity.setSellerId(warehouse.getSellerId());
        entity.setType(warehouse.getType().name());
        entity.setStatus(warehouse.getStatus().name());
        entity.setLocation(JpaCommonMapper.toEmbeddable(warehouse.getLocation()));
    }
}
