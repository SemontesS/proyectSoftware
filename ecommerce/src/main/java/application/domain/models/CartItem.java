package application.domain.models;

import application.domain.valueobjects.Money;

import java.util.Objects;

/**
 * CartItem represents un product seleccionado dentro de un cart y es
 * un elemento interno del added Cart (Domain Model — NexusMarket,
 * sección 13).
 */
public class CartItem {

    private final Long productId;
    private int quantity;
    private final Money unitPrice;

    public CartItem(Long productId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "productId es obligatorio");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice es obligatorio");
        setQuantity(quantity);
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity debe ser mayor a zero.");
        }
        this.quantity = quantity;
    }

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }

    public Long getProductId() {
        return productId;
    }

    public int getCantidad() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }
}
