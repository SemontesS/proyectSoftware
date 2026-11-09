package application.domain.ports.in;

import application.domain.models.*;
import application.domain.valueobjects.*;

import java.util.List;

/** Operations of the LOGISTICS_OPERATOR role. */
public interface LogisticsOperatorPort {
    // Account
    User consultUser(User requestingUser, Long userId);
    User updateUser(User requestingUser, Long userId, String name, Email email);
    void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword);
    Warehouse consultWarehouse(User requestingUser, Long warehouseId);
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

    // Orders and shipments
    Order consultOrder(User requestingUser, Long orderId);
    List<Shipment> prepareShipment(User requestingUser, Long orderId);
    Shipment dispatchShipment(User requestingUser, Long shipmentId);
    Shipment confirmDelivery(User requestingUser, Long shipmentId);
    Shipment consultShipment(User requestingUser, Long shipmentId);
    List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId);

    // Returns
    Return receiveReturn(User requestingUser, Long returnId);
}
