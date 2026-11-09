package application.domain.services.inventory;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.WarehouseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registers an inbound stock movement (INBOUND) in an active warehouse. */
@Service
@RequiredArgsConstructor
public class RegisterInventoryEntryService {
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Inventory register(User requestingUser, Long inventoryId, int quantity) {
        permissions.validate(requestingUser, Permission.INVENTORY_REGISTER_ENTRY);
        Inventory inventory = inventoryRepositoryPort.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory", inventoryId));
        resourceAccess.validateInventoryAccess(requestingUser, inventory);

        boolean active = warehouseRepositoryPort.findById(inventory.getWarehouseId())
                .map(warehouse -> warehouse.getStatus() == WarehouseStatus.ACTIVE)
                .orElse(false);
        if (!active) throw new BusinessRuleViolationException("Stock cannot enter an inactive warehouse.");

        inventory.recordInbound(quantity);
        return inventoryRepositoryPort.save(inventory);
    }
}
