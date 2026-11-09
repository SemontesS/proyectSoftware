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
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "seller_id")
    private Long sellerId;
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "location_type", length = 20)),
            @AttributeOverride(name = "detail", column = @Column(name = "location_detail", length = 255)),
            @AttributeOverride(name = "city", column = @Column(name = "location_city", length = 100))})
    private AddressEmbeddable location;
}
