package application.domain.ports.out;

import application.domain.models.Inventory;
import application.domain.valueobjects.Sku;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Long nextId();

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long inventoryId);

    List<Inventory> findByProductId(Long productId);

    List<Inventory> findByProductAndSku(Long productId, Sku sku);

    Optional<Inventory> findByProductSkuAndWarehouse(Long productId, Sku sku, Long warehouseId);

    List<Inventory> findByWarehouseId(Long warehouseId);
}
