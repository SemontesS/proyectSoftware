package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/** Returns (and their refunds) visible to the user: buyers see their own, sellers those of their products, staff all. */
@Service
@RequiredArgsConstructor
public class ConsultReturnService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public List<Return> consultVisible(User requestingUser) {
        permissions.validate(requestingUser, Permission.RETURN_CONSULT);
        return returnRepositoryPort.findAll().stream()
                .filter(returnRequest -> isVisible(requestingUser, returnRequest))
                .toList();
    }

    public Return consult(User requestingUser, Long returnId) {
        permissions.validate(requestingUser, Permission.RETURN_CONSULT);
        Return returnRequest = returnRepositoryPort.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
        if (!isVisible(requestingUser, returnRequest)) {
            throw new UnauthorizedOperationException("The user cannot access this resource.");
        }
        return returnRequest;
    }

    private boolean isVisible(User user, Return returnRequest) {
        if (user.getRole() == UserRole.SELLER) {
            return Objects.equals(returnRequest.getSellerId(), user.getUserId());
        }
        if (user.getRole() == UserRole.BUYER) {
            return orderRepositoryPort.findById(returnRequest.getOrderId())
                    .map(Order::getBuyerId)
                    .map(buyerId -> buyerId.equals(user.getUserId()))
                    .orElse(false);
        }
        return true;
    }
}
