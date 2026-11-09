package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Applies a signed correction to the available stock. It can never leave the stock negative. */
@Service
@RequiredArgsConstructor
public class AdjustInventoryService {
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Inventory adjust(User requestingUser, Long inventoryId, int delta) {
        permissions.validate(requestingUser, Permission.INVENTORY_ADJUST);
        Inventory inventory = inventoryRepositoryPort.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory", inventoryId));
        resourceAccess.validateInventoryAccess(requestingUser, inventory);
        inventory.recordAdjustment(delta);
        return inventoryRepositoryPort.save(inventory);
    }
}
