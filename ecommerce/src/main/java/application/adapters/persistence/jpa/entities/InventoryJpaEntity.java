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
@Table(name = "inventories", uniqueConstraints = @UniqueConstraint(
        name = "uk_inventory_product_sku_warehouse", columnNames = {"product_id", "sku", "warehouse_id"}))
@Getter
@Setter
@NoArgsConstructor
public class InventoryJpaEntity {
    @Id
    private Long id;
    /** Optimistic lock: two concurrent reservations of the same stock cannot both succeed. */
    @Version
    private Long version;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "sku", nullable = false, length = 64)
    private String sku;
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;
    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;
    @Column(name = "damaged_quantity", nullable = false)
    private int damagedQuantity;
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<InventoryMovementJpaEntity> movements = new ArrayList<>();
}
