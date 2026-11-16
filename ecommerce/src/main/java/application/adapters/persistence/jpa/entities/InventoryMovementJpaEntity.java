package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryJpaEntity inventory;
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}
