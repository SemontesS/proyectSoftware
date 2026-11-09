package application.domain.services.inventory;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.Sku;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Creates the inventory record of a physical product variant in a warehouse, with its initial stock. */
@Service
@RequiredArgsConstructor
public class RegisterInitialInventoryService {
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Inventory register(User requestingUser, Long productId, Sku sku, Long warehouseId, int initialQuantity) {
        permissions.validate(requestingUser, Permission.INVENTORY_REGISTER_INITIAL);
        if (initialQuantity < 0) throw new IllegalArgumentException("Initial quantity cannot be negative.");
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        resourceAccess.validateOwnerOrRole(requestingUser, product.getSellerId(),
                UserRole.ADMINISTRATOR, UserRole.LOGISTICS_OPERATOR);

        if (!product.requiresInventoryAndShipping()) {
            throw new BusinessRuleViolationException("Digital products do not have inventory.");
        }
        if (!product.hasVariant(sku)) {
            throw new BusinessRuleViolationException("The SKU does not belong to the product.");
        }
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
        if (warehouse.getStatus() != WarehouseStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Inventory cannot be registered in an inactive warehouse.");
        }
        if (warehouse.getType() == WarehouseType.SELLER && !warehouse.getSellerId().equals(product.getSellerId())) {
            throw new BusinessRuleViolationException("The warehouse belongs to a different seller.");
        }
        if (inventoryRepositoryPort.findByProductSkuAndWarehouse(productId, sku, warehouseId).isPresent()) {
            throw new DuplicateEntityException("Inventory already exists for this product variant in the warehouse.");
        }

        Inventory inventory = new Inventory(inventoryRepositoryPort.nextId(), productId, sku, warehouseId, 0);
        if (initialQuantity > 0) inventory.recordInbound(initialQuantity);
        return inventoryRepositoryPort.save(inventory);
    }
}
