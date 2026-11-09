package application.adapters.useCases;

import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.audit.ConsultAuditLogService;
import application.domain.services.audit.RecordAuditService;
import application.domain.services.billing.ConsultInvoiceService;
import application.domain.services.buyer.ConsultBuyerService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.logistics.ConsultShipmentService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.product.ConsultProductService;
import application.domain.services.reporting.ConsultAdministrativeReportsService;
import application.domain.services.seller.ConsultSellerService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.UpdateUserService;
import application.domain.services.warehouse.ConsultWarehouseService;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RawPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SupervisorUseCaseImpl implements SupervisorPort {
    private final ConsultUserService consultUserService;
    private final UpdateUserService updateUserService;
    private final ChangeUserPasswordService changeUserPasswordService;
    private final ConsultBuyerService consultBuyerService;
    private final ConsultSellerService consultSellerService;
    private final ConsultWarehouseService consultWarehouseService;
    private final ConsultProductService consultProductService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrderService consultOrderService;
    private final ConsultInvoiceService consultInvoiceService;
    private final ConsultShipmentService consultShipmentService;
    private final ConsultAdministrativeReportsService consultAdministrativeReportsService;
    private final ConsultAuditLogService consultAuditLogService;
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
    public Buyer consultBuyer(User requestingUser, Long buyerId) {
        return consultBuyerService.consult(requestingUser, buyerId);
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
    public Product consultProduct(User requestingUser, Long productId) {
        return consultProductService.consult(requestingUser, productId);
    }

    @Override
    public List<Product> consultCatalog(User requestingUser) {
        return consultProductService.consultCatalog(requestingUser);
    }

    @Override
    public List<Inventory> consultInventory(User requestingUser, Long productId, Long warehouseId) {
        return consultInventoryService.consult(requestingUser, productId, warehouseId);
    }

    @Override
    public Order consultOrder(User requestingUser, Long orderId) {
        return consultOrderService.consult(requestingUser, orderId);
    }

    @Override
    public List<Order> consultOrdersByBuyer(User requestingUser, Long buyerId) {
        return consultOrderService.consultByBuyer(requestingUser, buyerId);
    }

    @Override
    public Invoice consultInvoice(User requestingUser, Long orderId) {
        return consultInvoiceService.consultByOrder(requestingUser, orderId);
    }

    @Override
    public Shipment consultShipment(User requestingUser, Long shipmentId) {
        return consultShipmentService.consult(requestingUser, shipmentId);
    }

    @Override
    public List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId) {
        return consultShipmentService.consultByOrder(requestingUser, orderId);
    }

    @Override
    public AdministrativeReport consultAdministrativeReport(User requestingUser) {
        return consultAdministrativeReportsService.consult(requestingUser);
    }

    @Override
    public List<AuditLog> consultAuditLogByUser(User requestingUser, Long userId) {
        return consultAuditLogService.consultByUser(requestingUser, userId);
    }

    @Override
    public List<AuditLog> consultAuditLogByEntity(User requestingUser, String entityType, Long entityId) {
        return consultAuditLogService.consultByEntity(requestingUser, entityType, entityId);
    }
}
