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
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {
    private final InventoryJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("inventories");
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryJpaEntity entity = repository.findById(inventory.getInventoryId()).orElseGet(InventoryJpaEntity::new);
        InventoryJpaMapper.updateEntity(entity, inventory);
        repository.save(entity);
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(Long inventoryId) {
        return repository.findById(inventoryId).map(InventoryJpaMapper::toDomain);
    }

    @Override
    public List<Inventory> findByProductId(Long productId) {
        return repository.findByProductId(productId).stream().map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public List<Inventory> findByProductAndSku(Long productId, Sku sku) {
        return repository.findByProductIdAndSku(productId, sku.value()).stream()
                .map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public Optional<Inventory> findByProductSkuAndWarehouse(Long productId, Sku sku, Long warehouseId) {
        return repository.findByProductIdAndSkuAndWarehouseId(productId, sku.value(), warehouseId)
                .map(InventoryJpaMapper::toDomain);
    }

    @Override
    public List<Inventory> findByWarehouseId(Long warehouseId) {
        return repository.findByWarehouseId(warehouseId).stream().map(InventoryJpaMapper::toDomain).toList();
    }
}
