package application.domain.ports.in;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.*;
import application.domain.valueobjects.*;

import java.util.List;

/** Operations of the SUPERVISOR role (read-only follow-up). */
public interface SupervisorPort {
    // Account
    User consultUser(User requestingUser, Long userId);
    User updateUser(User requestingUser, Long userId, String name, Email email);
    void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword);

    // Consultations
    Buyer consultBuyer(User requestingUser, Long buyerId);
    Seller consultSeller(User requestingUser, Long sellerId);
    Warehouse consultWarehouse(User requestingUser, Long warehouseId);
    Product consultProduct(User requestingUser, Long productId);
    List<Product> consultCatalog(User requestingUser);
    List<Inventory> consultInventory(User requestingUser, Long productId, Long warehouseId);
    Order consultOrder(User requestingUser, Long orderId);
    List<Order> consultOrdersByBuyer(User requestingUser, Long buyerId);
    Invoice consultInvoice(User requestingUser, Long orderId);
    Shipment consultShipment(User requestingUser, Long shipmentId);
    List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId);
    AdministrativeReport consultAdministrativeReport(User requestingUser);
    List<AuditLog> consultAuditLogByUser(User requestingUser, Long userId);
    List<AuditLog> consultAuditLogByEntity(User requestingUser, AuditEntityType entityType, Long entityId);
}
