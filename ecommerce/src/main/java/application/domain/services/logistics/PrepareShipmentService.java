package application.domain.services.logistics;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Prepares the shipments of a paid order containing physical products: one shipment per warehouse
 * that fulfils lines of the order. The delivery address is the buyer's primary address.
 */
@Service
@RequiredArgsConstructor
public class PrepareShipmentService {
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public List<Shipment> prepare(User requestingUser, Long orderId) {
        permissions.validate(requestingUser, Permission.SHIPMENT_PREPARE);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessRuleViolationException("Only a paid order can be prepared for shipment.");
        }
        if (!order.hasPhysicalLines()) {
            throw new BusinessRuleViolationException("The order has no physical products to ship.");
        }
        if (!shipmentRepositoryPort.findByOrderId(orderId).isEmpty()) {
            throw new DuplicateEntityException("The order already has shipments.");
        }
        Buyer buyer = userRepositoryPort.findBuyerById(order.getBuyerId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer", order.getBuyerId()));

        return order.getLines().stream()
                .map(OrderLine::getWarehouseId)
                .filter(Objects::nonNull)
                .distinct()
                .map(warehouseId -> {
                    Shipment shipment = new Shipment(shipmentRepositoryPort.nextId(), orderId, warehouseId,
                            ShipmentStatus.PENDING, buyer.getPrimaryAddress());
                    shipment.prepare();
                    return shipmentRepositoryPort.save(shipment);
                })
                .toList();
    }
}
