package application.domain.services.warehouse;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.WarehouseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Activates or deactivates a warehouse. A warehouse with reserved stock cannot be deactivated. */
@Service
@RequiredArgsConstructor
public class ChangeWarehouseStatusService {
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Warehouse change(User requestingUser, Long warehouseId, WarehouseStatus status) {
        permissions.validate(requestingUser, Permission.WAREHOUSE_CHANGE_STATUS);
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
        if (status == WarehouseStatus.INACTIVE
                && inventoryRepositoryPort.findByWarehouseId(warehouseId).stream()
                        .anyMatch(inventory -> !inventory.getReservedQuantity().isZero())) {
            throw new BusinessRuleViolationException("A warehouse with reserved stock cannot be deactivated.");
        }
        warehouse.changeStatus(status);
        return warehouseRepositoryPort.save(warehouse);
    }
}
