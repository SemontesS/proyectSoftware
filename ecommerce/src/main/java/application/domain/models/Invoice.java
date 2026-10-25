package application.domain.models;

import application.domain.valueobjects.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Invoice {
    @EqualsAndHashCode.Include
    private final Long invoiceId;
    private final Long orderId;
    private final String number;
    private final LocalDate date;
    private final Money subtotal;
    private final Money total;

    public Invoice(Long invoiceId, Long orderId, String number, LocalDate date, Money subtotal, Money total) {
        this.invoiceId = Objects.requireNonNull(invoiceId, "Invoice ID is required.");
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required.");
        if (number == null || number.isBlank()) throw new IllegalArgumentException("Invoice number is required.");
        this.number = number.trim();
        this.date = Objects.requireNonNull(date, "Invoice date is required.");
        this.subtotal = Objects.requireNonNull(subtotal, "Invoice subtotal is required.");
        this.total = Objects.requireNonNull(total, "Invoice total is required.");
        if (!subtotal.currency().equals(total.currency())) throw new IllegalArgumentException("Invoice currency must match.");
        if (total.amount().compareTo(subtotal.amount()) < 0) throw new IllegalArgumentException("Invoice total cannot be less than subtotal.");
    }
}
