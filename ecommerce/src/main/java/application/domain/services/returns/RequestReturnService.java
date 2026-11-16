package application.domain.services.returns;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

/**
 * The buyer of a delivered order requests the return of ONE line of that order (a product/SKU).
 * The seller is taken from the order line. The rest of the order is not modified. A line cannot
 * have two live returns at the same time.
 */
@Service
@RequiredArgsConstructor
public class RequestReturnService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;
    private final Clock clock;

    public Return request(User requestingUser, Long orderId, Long productId, Sku sku, String reason) {
        permissions.validate(requestingUser, Permission.RETURN_REQUEST);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId());
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessRuleViolationException("A return requires a delivered order (status: " + order.getStatus() + ").");
        }
        OrderLine line = order.getLines().stream()
                .filter(candidate -> candidate.getProductId().equals(productId) && candidate.getSku().equals(sku))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order line of product " + productId, orderId));
        boolean alreadyReturned = returnRepositoryPort.findByOrderId(orderId).stream()
                .filter(other -> other.getProductId().equals(productId) && other.getSku().equals(sku))
                .anyMatch(other -> other.getStatus() != ReturnStatus.REJECTED
                        && other.getRefund().map(refund -> refund.getStatus() != RefundStatus.REJECTED).orElse(true));
        if (alreadyReturned) {
            throw new BusinessRuleViolationException("That product of the order already has a return in progress or completed.");
        }
        Return returnRequest = new Return(returnRepositoryPort.nextId(), orderId, productId, sku,
                line.getSellerId(), line.getQuantity(), reason, LocalDate.now(clock), ReturnStatus.REQUESTED);
        return returnRepositoryPort.save(returnRequest);
    }
}
