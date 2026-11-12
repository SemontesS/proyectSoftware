package application.adapters.useCases;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.InternalUser;
import application.domain.models.Inventory;
import application.domain.models.InventoryAllocation;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.SellerRegistration;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.services.audit.ConsultAuditLogService;
import application.domain.services.audit.RecordAuditService;
import application.domain.services.billing.ConsultInvoiceService;
import application.domain.services.billing.GenerateInvoiceService;
import application.domain.services.buyer.ChangeBuyerCommercialStatusService;
import application.domain.services.buyer.ConsultBuyerService;
import application.domain.services.buyer.UpdateBuyerInformationService;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.RegisterInitialInventoryService;
import application.domain.services.inventory.RegisterInventoryEntryService;
import application.domain.services.inventory.RegisterInventoryReturnService;
import application.domain.services.inventory.RegisterSaleDispatchService;
import application.domain.services.inventory.ReserveInventoryService;
import application.domain.services.logistics.ConfirmDeliveryService;
import application.domain.services.logistics.ConsultShipmentService;
import application.domain.services.logistics.DispatchShipmentService;
import application.domain.services.logistics.PrepareShipmentService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.order.UpdateOrderStatusService;
import application.domain.services.order.ValidateOrderPaymentService;
import application.domain.services.product.ConsultProductService;
import application.domain.services.product.DiscontinueProductService;
import application.domain.services.product.PublishProductService;
import application.domain.services.product.SuspendProductService;
import application.domain.services.reporting.ConsultAdministrativeReportsService;
import application.domain.services.returns.ApproveRefundService;
import application.domain.services.returns.GenerateRefundService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.returns.RejectRefundService;
import application.domain.services.seller.ChangeSellerStatusService;
import application.domain.services.seller.ConsultSellerService;
import application.domain.services.seller.RegisterSellerService;
import application.domain.services.seller.UpdateSellerInformationService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.RegisterUserService;
import application.domain.services.user.UpdateUserService;
import application.domain.services.warehouse.ChangeWarehouseStatusService;
import application.domain.services.warehouse.ConsultWarehouseService;
import application.domain.services.warehouse.RegisterWarehouseService;
import application.domain.services.warehouse.UpdateWarehouseService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.Sku;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdministratorUseCaseImpl implements AdministratorPort {
    private final RegisterUserService registerUserService;
    private final ConsultUserService consultUserService;
    private final UpdateUserService updateUserService;
    private final ChangeUserStatusService changeUserStatusService;
    private final ChangeUserPasswordService changeUserPasswordService;
    private final ConsultBuyerService consultBuyerService;
    private final UpdateBuyerInformationService updateBuyerInformationService;
    private final ChangeBuyerCommercialStatusService changeBuyerCommercialStatusService;
    private final RegisterSellerService registerSellerService;
    private final ConsultSellerService consultSellerService;
    private final UpdateSellerInformationService updateSellerInformationService;
    private final ChangeSellerStatusService changeSellerStatusService;
    private final RegisterWarehouseService registerWarehouseService;
    private final ConsultWarehouseService consultWarehouseService;
    private final UpdateWarehouseService updateWarehouseService;
    private final ChangeWarehouseStatusService changeWarehouseStatusService;
    private final PublishProductService publishProductService;
    private final SuspendProductService suspendProductService;
    private final DiscontinueProductService discontinueProductService;
    private final ConsultProductService consultProductService;
    private final RegisterInitialInventoryService registerInitialInventoryService;
    private final RegisterInventoryEntryService registerInventoryEntryService;
    private final ReserveInventoryService reserveInventoryService;
    private final RegisterSaleDispatchService registerSaleDispatchService;
    private final AdjustInventoryService adjustInventoryService;
    private final RegisterInventoryReturnService registerInventoryReturnService;
    private final ConsultInventoryService consultInventoryService;
    private final ValidateOrderPaymentService validateOrderPaymentService;
    private final UpdateOrderStatusService updateOrderStatusService;
    private final ConsultOrderService consultOrderService;
    private final GenerateInvoiceService generateInvoiceService;
    private final ConsultInvoiceService consultInvoiceService;
    private final PrepareShipmentService prepareShipmentService;
    private final DispatchShipmentService dispatchShipmentService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final ConsultShipmentService consultShipmentService;
    private final ReceiveReturnService receiveReturnService;
    private final GenerateRefundService generateRefundService;
    private final ApproveRefundService approveRefundService;
    private final RejectRefundService rejectRefundService;
    private final ConsultAdministrativeReportsService consultAdministrativeReportsService;
    private final ConsultAuditLogService consultAuditLogService;
    private final RecordAuditService audit;

    // ---- Users ----
    @Override
    public InternalUser registerInternalUser(User requestingUser, String name, Email email,
                                             IdentityDocument identityDocument, UserRole role, RawPassword password) {
        InternalUser user = registerUserService.register(requestingUser, name, email, identityDocument, role, password);
        audit.record(requestingUser, Permission.USER_REGISTER, AuditEntityType.USER, user.getUserId(), "Registered " + role);
        return user;
    }

    @Override
    public User consultUser(User requestingUser, Long userId) {
        return consultUserService.consult(requestingUser, userId);
    }

    @Override
    public User updateUser(User requestingUser, Long userId, String name, Email email) {
        User user = updateUserService.update(requestingUser, userId, name, email);
        audit.record(requestingUser, Permission.USER_UPDATE, AuditEntityType.USER, user.getUserId(), "User data updated");
        return user;
    }

    @Override
    public User changeUserStatus(User requestingUser, Long userId, UserStatus status) {
        User user = changeUserStatusService.change(requestingUser, userId, status);
        audit.record(requestingUser, Permission.USER_CHANGE_STATUS, AuditEntityType.USER, userId, "Status " + status);
        return user;
    }

    @Override
    public void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword) {
        changeUserPasswordService.change(requestingUser, currentPassword, newPassword);
        audit.record(requestingUser, Permission.USER_UPDATE, AuditEntityType.USER, requestingUser.getUserId(), "Password changed");
    }

    // ---- Buyers ----
    @Override
    public Buyer consultBuyer(User requestingUser, Long buyerId) {
        return consultBuyerService.consult(requestingUser, buyerId);
    }

    @Override
    public Buyer updateBuyerInformation(User requestingUser, Long buyerId, Address newPrimaryAddress,
                                        List<Address> addressesToAdd) {
        Buyer buyer = updateBuyerInformationService.update(requestingUser, buyerId, newPrimaryAddress, addressesToAdd);
        audit.record(requestingUser, Permission.BUYER_UPDATE, AuditEntityType.BUYER, buyerId, "Addresses updated");
        return buyer;
    }

    @Override
    public Buyer changeBuyerCommercialStatus(User requestingUser, Long buyerId, BuyerStatus status) {
        Buyer buyer = changeBuyerCommercialStatusService.change(requestingUser, buyerId, status);
        audit.record(requestingUser, Permission.BUYER_CHANGE_COMMERCIAL_STATUS, AuditEntityType.BUYER, buyerId, "Status " + status);
        return buyer;
    }

    // ---- Sellers ----
    @Override
    public SellerRegistration registerSeller(User requestingUser, String name, Email email,
                                             IdentityDocument identityDocument, Address firstWarehouseLocation,
                                             RawPassword password) {
        SellerRegistration registration = registerSellerService.register(requestingUser, name, email,
                identityDocument, firstWarehouseLocation, password);
        audit.record(requestingUser, Permission.SELLER_REGISTER, AuditEntityType.SELLER, registration.seller().getUserId(), name);
        return registration;
    }

    @Override
    public Seller consultSeller(User requestingUser, Long sellerId) {
        return consultSellerService.consult(requestingUser, sellerId);
    }

    @Override
    public Seller updateSellerInformation(User requestingUser, Long sellerId, String name, Email email) {
        Seller seller = updateSellerInformationService.update(requestingUser, sellerId, name, email);
        audit.record(requestingUser, Permission.SELLER_UPDATE, AuditEntityType.SELLER, sellerId, "Seller data updated");
        return seller;
    }

    @Override
    public Seller changeSellerStatus(User requestingUser, Long sellerId, SellerStatus status) {
        Seller seller = changeSellerStatusService.change(requestingUser, sellerId, status);
        audit.record(requestingUser, Permission.SELLER_CHANGE_STATUS, AuditEntityType.SELLER, sellerId, "Status " + status);
        return seller;
    }

    // ---- Warehouses ----
    @Override
    public Warehouse registerWarehouse(User requestingUser, WarehouseType type, Long sellerId, Address location) {
        Warehouse warehouse = registerWarehouseService.register(requestingUser, type, sellerId, location);
        audit.record(requestingUser, Permission.WAREHOUSE_REGISTER, AuditEntityType.WAREHOUSE, warehouse.getWarehouseId(), "Type " + type);
        return warehouse;
    }

    @Override
    public Warehouse consultWarehouse(User requestingUser, Long warehouseId) {
        return consultWarehouseService.consult(requestingUser, warehouseId);
    }

    @Override
    public Warehouse updateWarehouse(User requestingUser, Long warehouseId, Address location) {
        Warehouse warehouse = updateWarehouseService.update(requestingUser, warehouseId, location);
        audit.record(requestingUser, Permission.WAREHOUSE_UPDATE, AuditEntityType.WAREHOUSE, warehouseId, "Location updated");
        return warehouse;
    }

    @Override
    public Warehouse changeWarehouseStatus(User requestingUser, Long warehouseId, WarehouseStatus status) {
        Warehouse warehouse = changeWarehouseStatusService.change(requestingUser, warehouseId, status);
        audit.record(requestingUser, Permission.WAREHOUSE_CHANGE_STATUS, AuditEntityType.WAREHOUSE, warehouseId, "Status " + status);
        return warehouse;
    }

    // ---- Catalog ----
    @Override
    public Product publishProduct(User requestingUser, Long productId) {
        Product product = publishProductService.publish(requestingUser, productId);
        audit.record(requestingUser, Permission.PRODUCT_PUBLISH, AuditEntityType.PRODUCT, productId, "Product published");
        return product;
    }

    @Override
    public Product suspendProduct(User requestingUser, Long productId) {
        Product product = suspendProductService.suspend(requestingUser, productId);
        audit.record(requestingUser, Permission.PRODUCT_SUSPEND, AuditEntityType.PRODUCT, productId, "Product suspended");
        return product;
    }

    @Override
    public Product discontinueProduct(User requestingUser, Long productId) {
        Product product = discontinueProductService.discontinue(requestingUser, productId);
        audit.record(requestingUser, Permission.PRODUCT_DISCONTINUE, AuditEntityType.PRODUCT, productId, "Product discontinued");
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

    // ---- Inventory ----
    @Override
    public Inventory registerInitialInventory(User requestingUser, Long productId, Sku sku, Long warehouseId,
                                              int initialQuantity) {
        Inventory inventory = registerInitialInventoryService.register(requestingUser, productId, sku,
                warehouseId, initialQuantity);
        audit.record(requestingUser, Permission.INVENTORY_REGISTER_INITIAL, AuditEntityType.INVENTORY,
                inventory.getInventoryId(), "Initial stock " + initialQuantity);
        return inventory;
    }

    @Override
    public Inventory registerInventoryEntry(User requestingUser, Long inventoryId, int quantity) {
        Inventory inventory = registerInventoryEntryService.register(requestingUser, inventoryId, quantity);
        audit.record(requestingUser, Permission.INVENTORY_REGISTER_ENTRY, AuditEntityType.INVENTORY, inventoryId, "Entry " + quantity);
        return inventory;
    }

    @Override
    public List<InventoryAllocation> reserveInventory(User requestingUser, Long productId, Sku sku, int quantity) {
        List<InventoryAllocation> allocations = reserveInventoryService.reserve(requestingUser, productId, sku, quantity);
        audit.record(requestingUser, Permission.INVENTORY_RESERVE, AuditEntityType.PRODUCT, productId,
                "Reserved " + quantity + " of " + sku.value());
        return allocations;
    }

    @Override
    public Inventory registerSaleDispatch(User requestingUser, Long inventoryId, int quantity) {
        Inventory inventory = registerSaleDispatchService.register(requestingUser, inventoryId, quantity);
        audit.record(requestingUser, Permission.INVENTORY_REGISTER_SALE_DISPATCH, AuditEntityType.INVENTORY, inventoryId,
                "Sale dispatch " + quantity);
        return inventory;
    }

    @Override
    public Inventory adjustInventory(User requestingUser, Long inventoryId, int delta) {
        Inventory inventory = adjustInventoryService.adjust(requestingUser, inventoryId, delta);
        audit.record(requestingUser, Permission.INVENTORY_ADJUST, AuditEntityType.INVENTORY, inventoryId, "Adjustment " + delta);
        return inventory;
    }

    @Override
    public Inventory registerInventoryReturn(User requestingUser, Long inventoryId, int quantity) {
        Inventory inventory = registerInventoryReturnService.register(requestingUser, inventoryId, quantity);
        audit.record(requestingUser, Permission.INVENTORY_REGISTER_RETURN, AuditEntityType.INVENTORY, inventoryId,
                "Return to stock " + quantity);
        return inventory;
    }

    @Override
    public List<Inventory> consultInventory(User requestingUser, Long productId, Long warehouseId) {
        return consultInventoryService.consult(requestingUser, productId, warehouseId);
    }

    // ---- Orders, billing, shipments ----
    @Override
    public Order payOrder(User requestingUser, Long orderId) {
        Order order = validateOrderPaymentService.validate(requestingUser, orderId);
        audit.record(requestingUser, Permission.ORDER_VALIDATE_PAYMENT, AuditEntityType.ORDER, orderId, "Payment validated");
        return order;
    }

    @Override
    public Order updateOrderStatus(User requestingUser, Long orderId, OrderStatus target) {
        Order order = updateOrderStatusService.update(requestingUser, orderId, target);
        audit.record(requestingUser, Permission.ORDER_UPDATE_STATUS, AuditEntityType.ORDER, orderId, "Status " + target);
        return order;
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
    public Invoice generateInvoice(User requestingUser, Long orderId) {
        Invoice invoice = generateInvoiceService.generate(requestingUser, orderId);
        audit.record(requestingUser, Permission.INVOICE_GENERATE, AuditEntityType.INVOICE, invoice.getInvoiceId(),
                "Invoice for order " + orderId);
        return invoice;
    }

    @Override
    public Invoice consultInvoice(User requestingUser, Long orderId) {
        return consultInvoiceService.consultByOrder(requestingUser, orderId);
    }

    @Override
    public List<Shipment> prepareShipment(User requestingUser, Long orderId) {
        List<Shipment> shipments = prepareShipmentService.prepare(requestingUser, orderId);
        audit.record(requestingUser, Permission.SHIPMENT_PREPARE, AuditEntityType.ORDER, orderId,
                shipments.size() + " shipment(s) prepared");
        return shipments;
    }

    @Override
    public Shipment dispatchShipment(User requestingUser, Long shipmentId) {
        Shipment shipment = dispatchShipmentService.dispatch(requestingUser, shipmentId);
        audit.record(requestingUser, Permission.SHIPMENT_DISPATCH, AuditEntityType.SHIPMENT, shipmentId, "Shipment dispatched");
        return shipment;
    }

    @Override
    public Shipment confirmDelivery(User requestingUser, Long shipmentId) {
        Shipment shipment = confirmDeliveryService.confirm(requestingUser, shipmentId);
        audit.record(requestingUser, Permission.SHIPMENT_CONFIRM_DELIVERY, AuditEntityType.SHIPMENT, shipmentId, "Delivery confirmed");
        return shipment;
    }

    @Override
    public Shipment consultShipment(User requestingUser, Long shipmentId) {
        return consultShipmentService.consult(requestingUser, shipmentId);
    }

    @Override
    public List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId) {
        return consultShipmentService.consultByOrder(requestingUser, orderId);
    }

    // ---- Returns and refunds ----
    @Override
    public Return receiveReturn(User requestingUser, Long returnId) {
        Return returnRequest = receiveReturnService.receive(requestingUser, returnId);
        audit.record(requestingUser, Permission.RETURN_RECEIVE, AuditEntityType.RETURN, returnId, "Return received");
        return returnRequest;
    }

    @Override
    public Refund generateRefund(User requestingUser, Long returnId, Money amount) {
        Refund refund = generateRefundService.generate(requestingUser, returnId, amount);
        audit.record(requestingUser, Permission.REFUND_GENERATE, AuditEntityType.RETURN, returnId, "Refund " + amount.amount());
        return refund;
    }

    @Override
    public Refund approveRefund(User requestingUser, Long returnId) {
        Refund refund = approveRefundService.approve(requestingUser, returnId);
        audit.record(requestingUser, Permission.REFUND_APPROVE, AuditEntityType.RETURN, returnId, "Refund approved");
        return refund;
    }

    @Override
    public Refund rejectRefund(User requestingUser, Long returnId) {
        Refund refund = rejectRefundService.reject(requestingUser, returnId);
        audit.record(requestingUser, Permission.REFUND_REJECT, AuditEntityType.RETURN, returnId, "Refund rejected");
        return refund;
    }

    // ---- Reports and audit ----
    @Override
    public AdministrativeReport consultAdministrativeReport(User requestingUser) {
        return consultAdministrativeReportsService.consult(requestingUser);
    }

    @Override
    public List<AuditLog> consultAuditLogByUser(User requestingUser, Long userId) {
        return consultAuditLogService.consultByUser(requestingUser, userId);
    }

    @Override
    public List<AuditLog> consultAuditLogByEntity(User requestingUser, AuditEntityType entityType, Long entityId) {
        return consultAuditLogService.consultByEntity(requestingUser, entityType, entityId);
    }
}
