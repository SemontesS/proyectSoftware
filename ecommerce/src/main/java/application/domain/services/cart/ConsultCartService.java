package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** A buyer consults its own active cart. */
@Service
@RequiredArgsConstructor
public class ConsultCartService {
    private final CartRepositoryPort cartRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Cart consult(User requestingUser) {
        permissions.validate(requestingUser, Permission.CART_CONSULT);
        return cartRepositoryPort.findActiveByBuyerId(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Active cart", requestingUser.getUserId()));
    }
}
