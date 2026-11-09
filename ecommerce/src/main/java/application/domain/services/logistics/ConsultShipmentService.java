package application.domain.services.logistics;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** The buyer of the order and staff roles consult shipments. */
@Service
@RequiredArgsConstructor
public class ConsultShipmentService {
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Shipment consult(User requestingUser, Long shipmentId) {
        permissions.validate(requestingUser, Permission.SHIPMENT_CONSULT);
        Shipment shipment = shipmentRepositoryPort.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment", shipmentId));
        validateAccess(requestingUser, shipment.getOrderId());
        return shipment;
    }

    public List<Shipment> consultByOrder(User requestingUser, Long orderId) {
        permissions.validate(requestingUser, Permission.SHIPMENT_CONSULT);
        validateAccess(requestingUser, orderId);
        return shipmentRepositoryPort.findByOrderId(orderId);
    }

    private void validateAccess(User user, Long orderId) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(user, order.getBuyerId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR, UserRole.LOGISTICS_OPERATOR);
    }
}
