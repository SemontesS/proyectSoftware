package application.domain.services.logistics;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.services.inventory.RegisterSaleDispatchService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Dispatches a shipment: registers the stock outbound of its lines and, when every shipment of
 * the order has been dispatched, moves the order to SHIPPED.
 */
@Service
@RequiredArgsConstructor
public class DispatchShipmentService {
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RegisterSaleDispatchService registerSaleDispatchService;
    private final ValidateUserPermissionsService permissions;

    public Shipment dispatch(User requestingUser, Long shipmentId) {
        permissions.validate(requestingUser, Permission.SHIPMENT_DISPATCH);
        Shipment shipment = shipmentRepositoryPort.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment", shipmentId));
        Order order = orderRepositoryPort.findById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", shipment.getOrderId()));

        shipment.dispatch(); // validates PREPARING -> SHIPPED before touching the inventory
        for (OrderLine line : order.getLines()) {
            if (!shipment.getWarehouseId().equals(line.getWarehouseId())) continue;
            Inventory inventory = inventoryRepositoryPort
                    .findByProductSkuAndWarehouse(line.getProductId(), line.getSku(), line.getWarehouseId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory", line.getProductId()));
            registerSaleDispatchService.register(requestingUser, inventory.getInventoryId(), line.getQuantity());
        }
        shipmentRepositoryPort.save(shipment);

        boolean allDispatched = shipmentRepositoryPort.findByOrderId(order.getOrderId()).stream()
                .allMatch(s -> s.getStatus() == ShipmentStatus.SHIPPED || s.getStatus() == ShipmentStatus.DELIVERED);
        if (allDispatched && order.getStatus() == OrderStatus.PAID) {
            order.ship();
            orderRepositoryPort.save(order);
        }
        return shipment;
    }
}
