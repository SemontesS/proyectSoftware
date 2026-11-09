package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Consults the inventory of a product, optionally limited to one warehouse (null = all warehouses). */
@Service
@RequiredArgsConstructor
public class ConsultInventoryService {
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public List<Inventory> consult(User requestingUser, Long productId, Long warehouseId) {
        permissions.validate(requestingUser, Permission.INVENTORY_CONSULT);
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        resourceAccess.validateOwnerOrRole(requestingUser, product.getSellerId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR, UserRole.LOGISTICS_OPERATOR);
        return inventoryRepositoryPort.findByProductId(productId).stream()
                .filter(inventory -> warehouseId == null || warehouseId.equals(inventory.getWarehouseId()))
                .toList();
    }
}
