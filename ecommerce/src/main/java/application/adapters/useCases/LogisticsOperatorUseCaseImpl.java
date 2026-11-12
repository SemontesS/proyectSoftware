package application.adapters.useCases;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.Inventory;
import application.domain.models.InventoryAllocation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.services.audit.RecordAuditService;
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
import application.domain.services.product.ConsultProductService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.UpdateUserService;
import application.domain.services.warehouse.ConsultWarehouseService;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LogisticsOperatorUseCaseImpl implements LogisticsOperatorPort {
    private final ConsultUserService consultUserService;
    private final UpdateUserService updateUserService;
    private final ChangeUserPasswordService changeUserPasswordService;
    private final ConsultWarehouseService consultWarehouseService;
    private final ConsultProductService consultProductService;
    private final RegisterInitialInventoryService registerInitialInventoryService;
    private final RegisterInventoryEntryService registerInventoryEntryService;
    private final ReserveInventoryService reserveInventoryService;
    private final RegisterSaleDispatchService registerSaleDispatchService;
    private final AdjustInventoryService adjustInventoryService;
    private final RegisterInventoryReturnService registerInventoryReturnService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrderService consultOrderService;
    private final PrepareShipmentService prepareShipmentService;
    private final DispatchShipmentService dispatchShipmentService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final ConsultShipmentService consultShipmentService;
    private final ReceiveReturnService receiveReturnService;
    private final RecordAuditService audit;

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
    public void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword) {
        changeUserPasswordService.change(requestingUser, currentPassword, newPassword);
        audit.record(requestingUser, Permission.USER_UPDATE, AuditEntityType.USER, requestingUser.getUserId(), "Password changed");
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

    @Override
    public Order consultOrder(User requestingUser, Long orderId) {
        return consultOrderService.consult(requestingUser, orderId);
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

    @Override
    public Return receiveReturn(User requestingUser, Long returnId) {
        Return returnRequest = receiveReturnService.receive(requestingUser, returnId);
        audit.record(requestingUser, Permission.RETURN_RECEIVE, AuditEntityType.RETURN, returnId, "Return received");
        return returnRequest;
    }
}
