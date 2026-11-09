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
public class WarehouseRepositoryAdapter implements WarehouseRepositoryPort {
    private final WarehouseJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("warehouses");
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        WarehouseJpaEntity entity = repository.findById(warehouse.getWarehouseId()).orElseGet(WarehouseJpaEntity::new);
        WarehouseJpaMapper.updateEntity(entity, warehouse);
        repository.save(entity);
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(Long warehouseId) {
        return repository.findById(warehouseId).map(WarehouseJpaMapper::toDomain);
    }

    @Override
    public List<Warehouse> findBySellerId(Long sellerId) {
        return repository.findBySellerId(sellerId).stream().map(WarehouseJpaMapper::toDomain).toList();
    }
}
