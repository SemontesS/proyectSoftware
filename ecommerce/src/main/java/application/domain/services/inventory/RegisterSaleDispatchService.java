package application.domain.services.inventory;

import application.domain.valueobjects.Quantity;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registers the stock outbound of a dispatched sale (consumes the reservation first). */
@Service
@RequiredArgsConstructor
public class RegisterSaleDispatchService {
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Inventory register(User requestingUser, Long inventoryId, int quantity) {
        permissions.validate(requestingUser, Permission.INVENTORY_REGISTER_SALE_DISPATCH);
        Inventory inventory = inventoryRepositoryPort.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory", inventoryId));
        resourceAccess.validateInventoryAccess(requestingUser, inventory);
        inventory.recordSaleOutbound(Quantity.positive(quantity));
        return inventoryRepositoryPort.save(inventory);
    }
}
