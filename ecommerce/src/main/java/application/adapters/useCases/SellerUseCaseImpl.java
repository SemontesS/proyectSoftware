package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SellerPort;
import application.domain.services.audit.RecordAuditService;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.RegisterInitialInventoryService;
import application.domain.services.inventory.RegisterInventoryEntryService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.product.ConsultProductService;
import application.domain.services.product.DiscontinueProductService;
import application.domain.services.product.PublishProductService;
import application.domain.services.product.RegisterProductService;
import application.domain.services.product.SuspendProductService;
import application.domain.services.product.UpdateProductService;
import application.domain.services.seller.ConsultSellerService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.UpdateUserService;
import application.domain.services.warehouse.ConsultWarehouseService;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerUseCaseImpl implements SellerPort {
    private final ConsultUserService consultUserService;
    private final UpdateUserService updateUserService;
    private final ChangeUserPasswordService changeUserPasswordService;
    private final ConsultSellerService consultSellerService;
    private final ConsultWarehouseService consultWarehouseService;
    private final RegisterProductService registerProductService;
    private final UpdateProductService updateProductService;
    private final PublishProductService publishProductService;
    private final SuspendProductService suspendProductService;
    private final DiscontinueProductService discontinueProductService;
    private final ConsultProductService consultProductService;
    private final RegisterInitialInventoryService registerInitialInventoryService;
    private final RegisterInventoryEntryService registerInventoryEntryService;
    private final AdjustInventoryService adjustInventoryService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrderService consultOrderService;
    private final RecordAuditService audit;

    @Override
    public User consultUser(User requestingUser, Long userId) {
        return consultUserService.consult(requestingUser, userId);
    }

    @Override
    public User updateUser(User requestingUser, Long userId, String name, Email email) {
        User user = updateUserService.update(requestingUser, userId, name, email);
        audit.record(requestingUser, Permission.USER_UPDATE, "User", user.getUserId(), "User data updated");
        return user;
    }

    @Override
    public void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword) {
        changeUserPasswordService.change(requestingUser, currentPassword, newPassword);
        audit.record(requestingUser, Permission.USER_UPDATE, "User", requestingUser.getUserId(), "Password changed");
    }

    @Override
    public Seller consultSeller(User requestingUser, Long sellerId) {
        return consultSellerService.consult(requestingUser, sellerId);
    }

    @Override
    public Warehouse consultWarehouse(User requestingUser, Long warehouseId) {
        return consultWarehouseService.consult(requestingUser, warehouseId);
    }

    @Override
    public Product registerProduct(User requestingUser, String name, ProductType type, Money price,
                                   List<ProductVariant> variants) {
        Product product = registerProductService.register(requestingUser, name, type, price, variants);
        audit.record(requestingUser, Permission.PRODUCT_REGISTER, "Product", product.getProductId(), name);
        return product;
    }

    @Override
    public Product updateProduct(User requestingUser, Long productId, String name, Money price) {
        Product product = updateProductService.update(requestingUser, productId, name, price);
        audit.record(requestingUser, Permission.PRODUCT_UPDATE, "Product", productId, "Product updated");
        return product;
    }

    @Override
    public Product publishProduct(User requestingUser, Long productId) {
        Product product = publishProductService.publish(requestingUser, productId);
        audit.record(requestingUser, Permission.PRODUCT_PUBLISH, "Product", productId, "Product published");
        return product;
    }

    @Override
    public Product suspendProduct(User requestingUser, Long productId) {
        Product product = suspendProductService.suspend(requestingUser, productId);
        audit.record(requestingUser, Permission.PRODUCT_SUSPEND, "Product", productId, "Product suspended");
        return product;
    }

    @Override
    public Product discontinueProduct(User requestingUser, Long productId) {
        Product product = discontinueProductService.discontinue(requestingUser, productId);
        audit.record(requestingUser, Permission.PRODUCT_DISCONTINUE, "Product", productId, "Product discontinued");
        return product;
    }

    @Override
    public Product consultProduct(User requestingUser, Long productId) {
        return consultProductService.consult(requestingUser, productId);
    }

    @Override
    public List<Product> consultCatalog(User requestingUser) {
        return consultProductService.consultCatalog(requestingUser);
    }

    @Override
    public Inventory registerInitialInventory(User requestingUser, Long productId, Sku sku, Long warehouseId,
                                              int initialQuantity) {
        Inventory inventory = registerInitialInventoryService.register(requestingUser, productId, sku,
                warehouseId, initialQuantity);
        audit.record(requestingUser, Permission.INVENTORY_REGISTER_INITIAL, "Inventory",
                inventory.getInventoryId(), "Initial stock " + initialQuantity);
        return inventory;
    }

    @Override
    public Inventory registerInventoryEntry(User requestingUser, Long inventoryId, int quantity) {
        Inventory inventory = registerInventoryEntryService.register(requestingUser, inventoryId, quantity);
        audit.record(requestingUser, Permission.INVENTORY_REGISTER_ENTRY, "Inventory", inventoryId, "Entry " + quantity);
        return inventory;
    }

    @Override
    public Inventory adjustInventory(User requestingUser, Long inventoryId, int delta) {
        Inventory inventory = adjustInventoryService.adjust(requestingUser, inventoryId, delta);
        audit.record(requestingUser, Permission.INVENTORY_ADJUST, "Inventory", inventoryId, "Adjustment " + delta);
        return inventory;
    }

    @Override
    public List<Inventory> consultInventory(User requestingUser, Long productId, Long warehouseId) {
        return consultInventoryService.consult(requestingUser, productId, warehouseId);
    }

    @Override
    public Order consultOrder(User requestingUser, Long orderId) {
        return consultOrderService.consult(requestingUser, orderId);
    }
}
