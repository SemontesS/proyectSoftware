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
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    @ElementCollection
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "position")
    private List<OrderLineEmbeddable> lines = new ArrayList<>();
}
