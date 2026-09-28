package application.domain.models;

import application.domain.valueobjects.Money;

import java.util.Objects;

/**
 * OrderLine represents cada product incluido en un order y es un
 * elemento interno del added Order (Domain Model — NexusMarket,
 * sección 15).
 */
public class OrderLine {

    private final Long productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderLine(Long productId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "productId es obligatorio");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice es obligatorio");
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
