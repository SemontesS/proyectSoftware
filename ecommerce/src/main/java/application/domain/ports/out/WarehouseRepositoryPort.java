package application.domain.ports.out;

import application.domain.models.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepositoryPort {
    Long nextId();

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(Long warehouseId);

    List<Warehouse> findBySellerId(Long sellerId);
}
