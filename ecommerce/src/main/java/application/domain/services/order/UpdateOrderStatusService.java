package application.domain.services.order;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Manual status changes of an order: CANCELLED (buyer owner or administrator, before dispatch;
 * reserved stock is released) and COMPLETED (administrator, once delivered). PAID, SHIPPED and
 * DELIVERED are driven by the payment and logistics services.
 */
@Service
@RequiredArgsConstructor
public class UpdateOrderStatusService {
    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Order update(User requestingUser, Long orderId, OrderStatus target) {
        permissions.validate(requestingUser, Permission.ORDER_UPDATE_STATUS);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId(), UserRole.ADMINISTRATOR);

        switch (target) {
            case CANCELLED -> cancel(order);
            case COMPLETED -> {
                if (requestingUser.getRole() != UserRole.ADMINISTRATOR) {
                    throw new UnauthorizedOperationException("Only an administrator can complete an order.");
                }
                order.complete();
            }
            default -> throw new BusinessRuleViolationException(
                    "The status " + target + " is set by the payment and logistics services.");
        }
        return orderRepositoryPort.save(order);
    }

    private void cancel(Order order) {
        order.cancel(); // validates that the order has not been dispatched
        for (OrderLine line : order.getLines()) {
            if (line.getWarehouseId() == null) continue; // digital line: nothing reserved
            Inventory inventory = inventoryRepositoryPort
                    .findByProductSkuAndWarehouse(line.getProductId(), line.getSku(), line.getWarehouseId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory", line.getProductId()));
            inventory.releaseReservation(line.getQuantity());
            inventoryRepositoryPort.save(inventory);
        }
    }
}
