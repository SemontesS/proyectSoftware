package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Removes a product (all its variants) from the buyer's active cart. */
@Service
@RequiredArgsConstructor
public class RemoveProductFromCartService {
    private final CartRepositoryPort cartRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Cart remove(User requestingUser, Long productId) {
        permissions.validate(requestingUser, Permission.CART_MANAGE);
        Cart cart = cartRepositoryPort.findActiveByBuyerId(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Active cart", requestingUser.getUserId()));
        cart.removeItem(productId);
        return cartRepositoryPort.save(cart);
    }
}
