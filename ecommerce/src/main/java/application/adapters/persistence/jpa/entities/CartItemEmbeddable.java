package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CartItemEmbeddable {
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "sku", nullable = false, length = 64)
    private String sku;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "unit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitAmount;
    @Column(name = "unit_currency", nullable = false, length = 3)
    private String unitCurrency;

    public CartItemEmbeddable(Long productId, String sku, int quantity, BigDecimal unitAmount, String unitCurrency) {
        this.productId = productId;
        this.sku = sku;
        this.quantity = quantity;
        this.unitAmount = unitAmount;
        this.unitCurrency = unitCurrency;
    }
}
