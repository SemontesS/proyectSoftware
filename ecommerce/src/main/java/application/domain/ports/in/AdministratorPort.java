package application.domain.ports.in;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.*;
import application.domain.valueobjects.*;

import java.util.List;

/** Operations of the ADMINISTRATOR role. */
public interface AdministratorPort {
    // Users
    InternalUser registerInternalUser(User requestingUser, String name, Email email,
                                      IdentityDocument identityDocument, UserRole role, RawPassword password);
    User consultUser(User requestingUser, Long userId);
    User updateUser(User requestingUser, Long userId, String name, Email email);
    User changeUserStatus(User requestingUser, Long userId, UserStatus status);
    void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword);

    // Buyers
    Buyer consultBuyer(User requestingUser, Long buyerId);
    Buyer updateBuyerInformation(User requestingUser, Long buyerId, Address newPrimaryAddress, List<Address> addressesToAdd);
    Buyer changeBuyerCommercialStatus(User requestingUser, Long buyerId, BuyerStatus status);

    // Sellers
    SellerRegistration registerSeller(User requestingUser, String name, Email email,
                                                IdentityDocument identityDocument, Address firstWarehouseLocation,
                                                RawPassword password);
    Seller consultSeller(User requestingUser, Long sellerId);
    Seller updateSellerInformation(User requestingUser, Long sellerId, String name, Email email);
    Seller changeSellerStatus(User requestingUser, Long sellerId, SellerStatus status);

    // Warehouses
    Warehouse registerWarehouse(User requestingUser, WarehouseType type, Long sellerId, Address location);
    Warehouse consultWarehouse(User requestingUser, Long warehouseId);
    Warehouse updateWarehouse(User requestingUser, Long warehouseId, Address location);
    Warehouse changeWarehouseStatus(User requestingUser, Long warehouseId, WarehouseStatus status);

    // Catalog
    Product publishProduct(User requestingUser, Long productId);
    Product suspendProduct(User requestingUser, Long productId);
    Product discontinueProduct(User requestingUser, Long productId);
    Product consultProduct(User requestingUser, Long productId);
    List<Product> consultCatalog(User requestingUser);

    // Inventory
    Inventory registerInitialInventory(User requestingUser, Long productId, Sku sku, Long warehouseId, int initialQuantity);
    Inventory registerInventoryEntry(User requestingUser, Long inventoryId, int quantity);
    List<InventoryAllocation> reserveInventory(User requestingUser, Long productId, Sku sku, int quantity);
    Inventory registerSaleDispatch(User requestingUser, Long inventoryId, int quantity);
    Inventory adjustInventory(User requestingUser, Long inventoryId, int delta);
    Inventory registerInventoryReturn(User requestingUser, Long inventoryId, int quantity);
    List<Inventory> consultInventory(User requestingUser, Long productId, Long warehouseId);

    // Orders, billing and shipments
    Order payOrder(User requestingUser, Long orderId);
    Order updateOrderStatus(User requestingUser, Long orderId, OrderStatus target);
    Order consultOrder(User requestingUser, Long orderId);
    List<Order> consultOrdersByBuyer(User requestingUser, Long buyerId);
    Invoice generateInvoice(User requestingUser, Long orderId);
    Invoice consultInvoice(User requestingUser, Long orderId);
    List<Shipment> prepareShipment(User requestingUser, Long orderId);
    Shipment dispatchShipment(User requestingUser, Long shipmentId);
    Shipment confirmDelivery(User requestingUser, Long shipmentId);
    Shipment consultShipment(User requestingUser, Long shipmentId);
    List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId);

    // Returns and refunds
    Return receiveReturn(User requestingUser, Long returnId);
    Refund generateRefund(User requestingUser, Long returnId, Money amount);
    Refund approveRefund(User requestingUser, Long returnId);
    Refund rejectRefund(User requestingUser, Long returnId);

    // Reports and audit
    AdministrativeReport consultAdministrativeReport(User requestingUser);
    List<AuditLog> consultAuditLogByUser(User requestingUser, Long userId);
    List<AuditLog> consultAuditLogByEntity(User requestingUser, AuditEntityType entityType, Long entityId);
}
