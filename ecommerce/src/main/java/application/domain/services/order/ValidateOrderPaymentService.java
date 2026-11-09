package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Registers the payment of a PENDING_PAYMENT order (PAID). An order made only of digital
 * products is delivered immediately, so it goes straight to COMPLETED.
 *
 * <p>No payment gateway is modeled: the caller asserts that the payment was received.
 */
@Service
@RequiredArgsConstructor
public class ValidateOrderPaymentService {
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Order validate(User requestingUser, Long orderId) {
        permissions.validate(requestingUser, Permission.ORDER_VALIDATE_PAYMENT);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId(), UserRole.ADMINISTRATOR);

        order.confirmPayment();
        if (!order.hasPhysicalLines()) order.complete();
        return orderRepositoryPort.save(order);
    }
}
