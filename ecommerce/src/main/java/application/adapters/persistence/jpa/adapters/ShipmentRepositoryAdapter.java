package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.mappers.*;
import application.adapters.persistence.jpa.repositories.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class ShipmentRepositoryAdapter implements ShipmentRepositoryPort {
    private final ShipmentJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("shipments");
    }

    @Override
    public Shipment save(Shipment shipment) {
        ShipmentJpaEntity entity = repository.findById(shipment.getShipmentId()).orElseGet(ShipmentJpaEntity::new);
        ShipmentJpaMapper.updateEntity(entity, shipment);
        repository.save(entity);
        return shipment;
    }

    @Override
    public Optional<Shipment> findById(Long shipmentId) {
        return repository.findById(shipmentId).map(ShipmentJpaMapper::toDomain);
    }

    @Override
    public List<Shipment> findByOrderId(Long orderId) {
        return repository.findByOrderIdOrderByIdAsc(orderId).stream().map(ShipmentJpaMapper::toDomain).toList();
    }
}
