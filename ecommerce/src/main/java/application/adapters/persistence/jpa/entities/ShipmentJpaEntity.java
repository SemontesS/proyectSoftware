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
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class ShipmentJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "delivery_address_type", length = 20)),
            @AttributeOverride(name = "detail", column = @Column(name = "delivery_address_detail", length = 255)),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_address_city", length = 100))})
    private AddressEmbeddable deliveryAddress;
}
