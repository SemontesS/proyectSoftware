package application.domain.services.returns;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ReturnStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

/**
 * The buyer of a delivered order requests a return (status REQUESTED). An order may originate
 * several returns. Requesting a return does not modify the order.
 */
@Service
@RequiredArgsConstructor
public class RequestReturnService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;
    private final Clock clock;

    public Return request(User requestingUser, Long orderId, String reason) {
        permissions.validate(requestingUser, Permission.RETURN_REQUEST);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId());
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessRuleViolationException("A return requires a delivered order (status: " + order.getStatus() + ").");
        }
        Return returnRequest = new Return(returnRepositoryPort.nextId(), orderId, reason,
                LocalDate.now(clock), ReturnStatus.REQUESTED);
        return returnRepositoryPort.save(returnRequest);
    }
}
