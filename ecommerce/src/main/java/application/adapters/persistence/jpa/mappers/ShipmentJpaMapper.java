package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class ShipmentJpaMapper {

    private ShipmentJpaMapper() {
    }

    public static Shipment toDomain(ShipmentJpaEntity entity) {
        return new Shipment(entity.getId(), entity.getOrderId(), entity.getWarehouseId(),
                ShipmentStatus.valueOf(entity.getStatus()), JpaCommonMapper.toDomain(entity.getDeliveryAddress()));
    }

    public static void updateEntity(ShipmentJpaEntity entity, Shipment shipment) {
        entity.setId(shipment.getShipmentId());
        entity.setOrderId(shipment.getOrderId());
        entity.setWarehouseId(shipment.getWarehouseId());
        entity.setStatus(shipment.getStatus().name());
        entity.setDeliveryAddress(JpaCommonMapper.toEmbeddable(shipment.getDeliveryAddress()));
    }
}
