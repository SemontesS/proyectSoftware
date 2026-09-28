package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.CartStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cart es un Aggregate Root (Domain Model — NexusMarket, sección 20).
 * Represents el espacio temporal donde el buyer selecciona products
 * antes de confirm la compra.
 *
 * Rule: un buyer puede tener zero o un cart activo
 * (esa unicidad se applies a nivel de caso de uso / repositorio, no dentro
 * de esta entidad).
 */
public class Cart {

    private final Long cartId;
    private final Long buyerId;
    private final LocalDateTime createdAt;
    private CartStatus status;
    private final List<CartItem> items = new ArrayList<>();
    private final String currency;

    public Cart(Long cartId, Long buyerId, LocalDateTime createdAt, String currency) {
        this.cartId = Objects.requireNonNull(cartId, "cartId es obligatorio");
        this.buyerId = Objects.requireNonNull(buyerId, "buyerId es obligatorio");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        this.status = CartStatus.ACTIVE;
        this.currency = Objects.requireNonNull(currency, "currency es obligatoria");
    }

    public void addItem(CartItem item) {
        validateActive();
        items.add(Objects.requireNonNull(item));
    }

    public void removeItem(Long productId) {
        validateActive();
        items.removeIf(i -> i.getProductId().equals(productId));
    }

    /** Confirms el cart, dejándolo listo para generar un Order. */
    public void confirm() {
        validateActive();
        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede confirm un cart sin products.");
        }
        this.status = CartStatus.CONFIRMED;
    }

    public void abandon() {
        this.status = CartStatus.ABANDONED;
    }

    public Money getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(Money.of(0, currency), Money::add);
    }

    private void validateActive() {
        if (status != CartStatus.ACTIVE) {
            throw new IllegalStateException("El cart no está activo.");
        }
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public CartStatus getStatus() {
        return status;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart cart)) return false;
        return Objects.equals(cartId, cart.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }
}
