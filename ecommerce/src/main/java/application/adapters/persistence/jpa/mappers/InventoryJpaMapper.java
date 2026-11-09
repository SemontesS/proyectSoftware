package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class InventoryJpaMapper {

    private InventoryJpaMapper() {
    }

    public static Inventory toDomain(InventoryJpaEntity entity) {
        List<InventoryMovement> movements = entity.getMovements().stream()
                .map(m -> new InventoryMovement(m.getId(), InventoryMovementType.valueOf(m.getType()),
                        m.getQuantity(), m.getOccurredAt()))
                .toList();
        return Inventory.restore(entity.getId(), entity.getProductId(), new Sku(entity.getSku()),
                entity.getWarehouseId(), entity.getAvailableQuantity(), entity.getReservedQuantity(),
                entity.getDamagedQuantity(), movements);
    }

    /**
     * Copies the state and appends the movements not stored yet. The movement history is
     * append-only, so saving the same inventory twice never duplicates rows.
     */
    public static void updateEntity(InventoryJpaEntity entity, Inventory inventory) {
        entity.setId(inventory.getInventoryId());
        entity.setProductId(inventory.getProductId());
        entity.setSku(inventory.getSku().value());
        entity.setWarehouseId(inventory.getWarehouseId());
        entity.setAvailableQuantity(inventory.getAvailableQuantity());
        entity.setReservedQuantity(inventory.getReservedQuantity());
        entity.setDamagedQuantity(inventory.getDamagedQuantity());

        List<InventoryMovement> movements = inventory.getMovements();
        for (int i = entity.getMovements().size(); i < movements.size(); i++) {
            InventoryMovement movement = movements.get(i);
            InventoryMovementJpaEntity row = new InventoryMovementJpaEntity();
            row.setInventory(entity);
            row.setType(movement.getType().name());
            row.setQuantity(movement.getQuantity());
            row.setOccurredAt(movement.getDate());
            entity.getMovements().add(row);
        }
    }
}
