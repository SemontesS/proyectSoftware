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

/** A return and its optional refund (the refund belongs to the Return aggregate). */
@Entity
@Table(name = "product_returns")
@Getter
@Setter
@NoArgsConstructor
public class ReturnJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;
    @Column(name = "return_date", nullable = false)
    private LocalDate date;
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "refund_id")
    private Long refundId;
    @Column(name = "refund_amount", precision = 19, scale = 2)
    private BigDecimal refundAmount;
    @Column(name = "refund_currency", length = 3)
    private String refundCurrency;
    @Column(name = "refund_date")
    private LocalDate refundDate;
    @Column(name = "refund_status", length = 20)
    private String refundStatus;
}
