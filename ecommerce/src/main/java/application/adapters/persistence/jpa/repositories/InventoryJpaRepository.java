package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, Long> {
    List<InventoryJpaEntity> findByProductId(Long productId);

    List<InventoryJpaEntity> findByProductIdAndSku(Long productId, String sku);

    Optional<InventoryJpaEntity> findByProductIdAndSkuAndWarehouseId(Long productId, String sku, Long warehouseId);

    List<InventoryJpaEntity> findByWarehouseId(Long warehouseId);
}
