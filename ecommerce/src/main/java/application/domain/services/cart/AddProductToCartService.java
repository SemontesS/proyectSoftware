package application.domain.services.cart;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Adds a published product variant to the active cart of an enabled buyer, creating the cart if needed. */
@Service
@RequiredArgsConstructor
public class AddProductToCartService {
    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Cart add(User requestingUser, Long productId, Sku sku, int quantity) {
        permissions.validate(requestingUser, Permission.CART_MANAGE);
        Buyer buyer = userRepositoryPort.findBuyerById(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer", requestingUser.getUserId()));
        if (buyer.getCommercialStatus() != BuyerStatus.ENABLED) {
            throw new BusinessRuleViolationException("The buyer is restricted and cannot purchase.");
        }
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        if (!product.isPublished()) throw new BusinessRuleViolationException("The product is not available.");
        if (!product.hasVariant(sku)) throw new BusinessRuleViolationException("The SKU does not belong to the product.");

        Cart cart = cartRepositoryPort.findActiveByBuyerId(buyer.getUserId())
                .orElseGet(() -> new Cart(cartRepositoryPort.nextId(), buyer.getUserId(), null,
                        product.getCurrentPrice().currency()));
        cart.addItem(new CartItem(productId, sku, quantity, product.getCurrentPrice()));
        return cartRepositoryPort.save(cart);
    }
}
