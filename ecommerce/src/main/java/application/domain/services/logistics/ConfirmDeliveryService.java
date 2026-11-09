package application.domain.services.logistics;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Confirms the delivery of a shipment; when all shipments are delivered the order is delivered and closed. */
@Service
@RequiredArgsConstructor
public class ConfirmDeliveryService {
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Shipment confirm(User requestingUser, Long shipmentId) {
        permissions.validate(requestingUser, Permission.SHIPMENT_CONFIRM_DELIVERY);
        Shipment shipment = shipmentRepositoryPort.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment", shipmentId));
        Order order = orderRepositoryPort.findById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", shipment.getOrderId()));

        shipment.confirmDelivery();
        shipmentRepositoryPort.save(shipment);

        boolean allDelivered = shipmentRepositoryPort.findByOrderId(order.getOrderId()).stream()
                .allMatch(s -> s.getStatus() == ShipmentStatus.DELIVERED);
        if (allDelivered && order.getStatus() == OrderStatus.SHIPPED) {
            order.deliver();
            order.complete();
            orderRepositoryPort.save(order);
        }
        return shipment;
    }
}
