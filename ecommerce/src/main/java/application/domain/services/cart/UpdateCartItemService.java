package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Changes the quantity of an item of the buyer's active cart (use RemoveProductFromCart to remove it). */
@Service
@RequiredArgsConstructor
public class UpdateCartItemService {
    private final CartRepositoryPort cartRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Cart update(User requestingUser, Long productId, Sku sku, int quantity) {
        permissions.validate(requestingUser, Permission.CART_MANAGE);
        Cart cart = cartRepositoryPort.findActiveByBuyerId(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Active cart", requestingUser.getUserId()));
        cart.updateItemQuantity(productId, sku, quantity);
        return cartRepositoryPort.save(cart);
    }
}
