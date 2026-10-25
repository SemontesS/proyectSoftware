package application.domain.models;

import application.domain.valueobjects.CartStatus;
import application.domain.valueobjects.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final String currency;

    public Cart(Long cartId, Long buyerId, LocalDateTime createdAt, String currency) {
        this.cartId = Objects.requireNonNull(cartId, "Cart ID is required.");
        this.buyerId = Objects.requireNonNull(buyerId, "Buyer ID is required.");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("Currency is required.");
        this.currency = currency.trim().toUpperCase();
        this.status = CartStatus.ACTIVE;
    }

    public void addItem(CartItem item) {
        validateActive();
        Objects.requireNonNull(item, "Cart item is required.");
        if (!currency.equals(item.getUnitPrice().currency())) {
            throw new IllegalArgumentException("Cart item currency must match cart currency.");
        }
        items.add(item);
    }

    public void removeItem(Long productId) {
        validateActive();
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void confirm() {
        validateActive();
        if (items.isEmpty()) throw new IllegalStateException("An empty cart cannot be confirmed.");
        status = CartStatus.CONFIRMED;
    }

    public void abandon() {
        validateActive();
        status = CartStatus.ABANDONED;
    }

    public Money getTotal() {
        return items.stream().map(CartItem::getSubtotal).reduce(Money.of(0, currency), Money::add);
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    private void validateActive() {
        if (status != CartStatus.ACTIVE) throw new IllegalStateException("Only an active cart can be modified.");
    }
}
