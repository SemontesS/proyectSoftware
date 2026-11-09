package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** A buyer sees its own orders, a seller the orders containing its products, staff roles any order. */
@Service
@RequiredArgsConstructor
public class ConsultOrderService {
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Order consult(User requestingUser, Long orderId) {
        permissions.validate(requestingUser, Permission.ORDER_CONSULT);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (requestingUser.getRole() == UserRole.SELLER) {
            boolean hasOwnProducts = order.getLines().stream()
                    .anyMatch(line -> requestingUser.getUserId().equals(line.getSellerId()));
            if (!hasOwnProducts) throw new UnauthorizedOperationException("The user cannot access this resource.");
        } else {
            resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId(),
                    UserRole.ADMINISTRATOR, UserRole.SUPERVISOR, UserRole.LOGISTICS_OPERATOR);
        }
        return order;
    }

    public List<Order> consultByBuyer(User requestingUser, Long buyerId) {
        permissions.validate(requestingUser, Permission.ORDER_CONSULT);
        resourceAccess.validateOwnerOrRole(requestingUser, buyerId,
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR, UserRole.LOGISTICS_OPERATOR);
        return orderRepositoryPort.findByBuyerId(buyerId);
    }
}
