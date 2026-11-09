package application.domain.ports.in;

import application.domain.models.*;
import application.domain.valueobjects.*;

import java.util.List;

/** Operations of the SELLER role. */
public interface SellerPort {
    // Account
    User consultUser(User requestingUser, Long userId);
    User updateUser(User requestingUser, Long userId, String name, Email email);
    void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword);
    Seller consultSeller(User requestingUser, Long sellerId);
    Warehouse consultWarehouse(User requestingUser, Long warehouseId);

    // Catalog
    Product registerProduct(User requestingUser, String name, ProductType type, Money price, List<ProductVariant> variants);
    Product updateProduct(User requestingUser, Long productId, String name, Money price);
    Product publishProduct(User requestingUser, Long productId);
    Product suspendProduct(User requestingUser, Long productId);
    Product discontinueProduct(User requestingUser, Long productId);
    Product consultProduct(User requestingUser, Long productId);
    List<Product> consultCatalog(User requestingUser);

    // Inventory
    Inventory registerInitialInventory(User requestingUser, Long productId, Sku sku, Long warehouseId, int initialQuantity);
    Inventory registerInventoryEntry(User requestingUser, Long inventoryId, int quantity);
    Inventory adjustInventory(User requestingUser, Long inventoryId, int delta);
    List<Inventory> consultInventory(User requestingUser, Long productId, Long warehouseId);

    // Orders
    Order consultOrder(User requestingUser, Long orderId);
}
