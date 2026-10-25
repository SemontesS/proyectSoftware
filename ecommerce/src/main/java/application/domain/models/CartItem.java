package application.domain.models;

import application.domain.valueobjects.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItem {
    @EqualsAndHashCode.Include
    private final Long productId;
    private final String sku;
    private int quantity;
    private final Money unitPrice;

    public CartItem(Long productId, int quantity, Money unitPrice) {
        this(productId, "DEFAULT", quantity, unitPrice);
    }

    public CartItem(Long productId, String sku, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required.");
        this.sku = sku.trim();
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price is required.");
        changeQuantity(quantity);
    }

    public void changeQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Cart item quantity must be greater than zero.");
        this.quantity = quantity;
    }

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }

    /** Legacy accessor retained because the untouched domain-service files use it. */
    public int getCantidad() {
        return quantity;
    }
}
