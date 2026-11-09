package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.Sku;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderLine {
    @EqualsAndHashCode.Include
    private final Long productId;
    @EqualsAndHashCode.Include
    private final Sku sku;
    private final Long sellerId;
    /** Warehouse that fulfils the line; null for digital products. */
    @EqualsAndHashCode.Include
    private final Long warehouseId;
    private final int quantity;
    private final Money unitPrice;

    public OrderLine(Long productId, Sku sku, Long sellerId, Long warehouseId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        this.sku = Objects.requireNonNull(sku, "SKU is required.");
        if (quantity <= 0) throw new IllegalArgumentException("Order line quantity must be greater than zero.");
        this.sellerId = sellerId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price is required.");
    }

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }
}
