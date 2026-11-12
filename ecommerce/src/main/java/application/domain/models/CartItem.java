package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.Quantity;
import application.domain.valueobjects.Sku;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItem {
    @EqualsAndHashCode.Include
    private final Long productId;
    @EqualsAndHashCode.Include
    private final Sku sku;
    private Quantity quantity;
    private final Money unitPrice;

    public CartItem(Long productId, Sku sku, Quantity quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        this.sku = Objects.requireNonNull(sku, "SKU is required.");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price is required.");
        changeQuantity(quantity);
    }

    public void changeQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity, "Cart item quantity is required.");
        if (quantity.isZero()) throw new IllegalArgumentException("Cart item quantity must be greater than zero.");
        this.quantity = quantity;
    }

    public Money getSubtotal() {
        return unitPrice.multiply(quantity.value());
    }
}
