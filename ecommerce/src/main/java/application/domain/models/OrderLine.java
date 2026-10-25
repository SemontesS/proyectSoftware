package application.domain.models;

import application.domain.valueobjects.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderLine {
    @EqualsAndHashCode.Include
    private final Long productId;
    private final String sku;
    private final Long sellerId;
    private final Long warehouseId;
    private final int quantity;
    private final Money unitPrice;

    public OrderLine(Long productId, int quantity, Money unitPrice) {
        this(productId, "DEFAULT", null, null, quantity, unitPrice);
    }

    public OrderLine(Long productId, String sku, Long sellerId, Long warehouseId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required.");
        if (quantity <= 0) throw new IllegalArgumentException("Order line quantity must be greater than zero.");
        this.sku = sku.trim();
        this.sellerId = sellerId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price is required.");
    }

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }

    /** Legacy accessor retained because the untouched domain-service files use it. */
    public int getCantidad() {
        return quantity;
    }
}
