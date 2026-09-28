package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Refund represents la return del money associated a una return
 * procesada. A Return puede generar como máximo un Refund
 * (Domain Model — NexusMarket, sección 19).
 */
public class Refund {

    private final Long refundId;
    private final Long returnId;
    private final Money amount;
    private final LocalDate date;
    private RefundStatus status;

    public Refund(Long refundId, Long returnId, Money amount, LocalDate date, RefundStatus status) {
        this.refundId = Objects.requireNonNull(refundId, "refundId es obligatorio");
        this.returnId = Objects.requireNonNull(returnId, "returnId es obligatorio");
        this.amount = Objects.requireNonNull(amount, "amount es obligatorio");
        this.date = Objects.requireNonNull(date, "date es obligatoria");
        this.status = Objects.requireNonNull(status, "status es obligatorio");
    }

    public void changeStatus(RefundStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus);
    }

    public Long getRefundId() {
        return refundId;
    }

    public Long getReturnId() {
        return returnId;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public RefundStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Refund that)) return false;
        return Objects.equals(refundId, that.refundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }
}
