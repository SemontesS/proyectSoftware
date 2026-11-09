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
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;
    @Column(name = "invoice_number", nullable = false, unique = true, length = 30)
    private String number;
    @Column(name = "invoice_date", nullable = false)
    private LocalDate date;
    @Column(name = "subtotal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotalAmount;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
}
