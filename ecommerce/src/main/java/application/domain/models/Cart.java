package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.CartStatus;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.Quantity;
import application.domain.valueobjects.Sku;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cart {
    @EqualsAndHashCode.Include
    private final Long cartId;
    private final Long buyerId;
    private final LocalDateTime createdAt;
    private CartStatus status;
    @Getter(AccessLevel.NONE)
    private final List<CartItem> items = new ArrayList<>();
    private final Currency currency;

    public Cart(Long cartId, Long buyerId, LocalDateTime createdAt, Currency currency) {
        this.cartId = Objects.requireNonNull(cartId, "Cart ID is required.");
        this.buyerId = Objects.requireNonNull(buyerId, "Buyer ID is required.");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        this.currency = Objects.requireNonNull(currency, "Currency is required.");
        this.status = CartStatus.ACTIVE;
    }

    /** Adds the item; if the same product variant is already in the cart the quantities are added. */
    public void addItem(CartItem item) {
        validateActive();
        Objects.requireNonNull(item, "Cart item is required.");
        if (currency != item.getUnitPrice().currency()) {
            throw new BusinessRuleViolationException("Cart item currency must match cart currency.");
        }
        findItem(item.getProductId(), item.getSku()).ifPresentOrElse(
                existing -> existing.changeQuantity(existing.getQuantity().plus(item.getQuantity())),
                () -> items.add(item));
    }

    public Optional<CartItem> findItem(Long productId, Sku sku) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId) && item.getSku().equals(sku))
                .findFirst();
    }

    public void updateItemQuantity(Long productId, Sku sku, Quantity quantity) {
        validateActive();
        CartItem item = findItem(productId, sku)
                .orElseThrow(() -> new BusinessRuleViolationException("The product variant is not in the cart."));
        item.changeQuantity(quantity);
    }

    /** Removes every variant of the product from the cart. */
    public void removeItem(Long productId) {
        validateActive();
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (!removed) throw new BusinessRuleViolationException("The product is not in the cart.");
    }

    /** Throws if the cart cannot be confirmed (not active or empty). Has no side effects. */
    public void validateConfirmable() {
        validateActive();
        if (items.isEmpty()) throw new BusinessRuleViolationException("An empty cart cannot be confirmed.");
    }

    public void confirm() {
        validateConfirmable();
        status = CartStatus.CONFIRMED;
    }

    public void abandon() {
        validateActive();
        status = CartStatus.ABANDONED;
    }

    public Money getTotal() {
        return items.stream().map(CartItem::getSubtotal).reduce(Money.zero(currency), Money::add);
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    private void validateActive() {
        if (status != CartStatus.ACTIVE) throw new BusinessRuleViolationException("Only an active cart can be modified.");
    }

    /** Rebuilds a persisted cart in any status. For persistence adapters only. */
    public static Cart restore(Long cartId, Long buyerId, LocalDateTime createdAt, CartStatus status,
                               Currency currency, List<CartItem> items) {
        Cart cart = new Cart(cartId, buyerId, createdAt, currency);
        cart.status = Objects.requireNonNull(status, "Cart status is required.");
        cart.items.addAll(items);
        return cart;
    }
}
