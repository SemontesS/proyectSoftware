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

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Column(name = "price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAmount;
    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;
    @ElementCollection
    @CollectionTable(name = "product_variants", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "position")
    private List<VariantEmbeddable> variants = new ArrayList<>();
}
