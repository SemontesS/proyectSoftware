package application.domain.models;

import application.domain.valueobjects.Money;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Invoice represents la información commercial generada como resultado de
 * un order. A Order genera una Invoice (Domain Model — NexusMarket,
 * sección 16).
 */
public class Invoice {

    private final Long invoiceId;
    private final Long orderId;
    private final String number;
    private final LocalDate date;
    private final Money subtotal;
    private final Money total;

    public Invoice(Long invoiceId, Long orderId, String number, LocalDate date,
                    Money subtotal, Money total) {
        this.invoiceId = Objects.requireNonNull(invoiceId, "invoiceId es obligatorio");
        this.orderId = Objects.requireNonNull(orderId, "orderId es obligatorio");
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("number es obligatorio");
        }
        this.number = number;
        this.date = Objects.requireNonNull(date, "date es obligatoria");
        this.subtotal = Objects.requireNonNull(subtotal, "subtotal es obligatorio");
        this.total = Objects.requireNonNull(total, "total es obligatorio");
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public Money getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice invoice)) return false;
        return Objects.equals(invoiceId, invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }
}
