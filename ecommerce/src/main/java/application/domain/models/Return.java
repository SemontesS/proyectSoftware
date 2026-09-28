package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.RefundStatus;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Return represents el proceso mediante el cual un buyer solicita
 * devolver un order (o parte de él) de agreement with las condiciones del
 * negocio. A order puede originar zero o más devoluciones; una
 * return puede generar como máximo un refund (Domain Model —
 * NexusMarket, sección 18 / 21).
 */
public class Return {

    private final Long returnId;
    private final Long orderId;
    private final String reason;
    private final LocalDate date;
    private ReturnStatus status;
    private Refund refund; // como máximo uno

    public Return(Long returnId, Long orderId, String reason, LocalDate date, ReturnStatus status) {
        this.returnId = Objects.requireNonNull(returnId, "returnId es obligatorio");
        this.orderId = Objects.requireNonNull(orderId, "orderId es obligatorio");
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason es obligatorio");
        }
        this.reason = reason;
        this.date = Objects.requireNonNull(date, "date es obligatoria");
        this.status = Objects.requireNonNull(status, "status es obligatorio");
    }

    public void changeStatus(ReturnStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus);
    }

    /** Generates el refund associated. A return solo puede generar uno. */
    public Refund generateRefund(Long refundId, Money amount, LocalDate date, RefundStatus initialStatus) {
        if (this.refund != null) {
            throw new IllegalStateException("This return ya generó un refund.");
        }
        this.refund = new Refund(refundId, this.returnId, amount, date, initialStatus);
        return this.refund;
    }

    public Long getReturnId() {
        return returnId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public Optional<Refund> getRefund() {
        return Optional.ofNullable(refund);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Return that)) return false;
        return Objects.equals(returnId, that.returnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }
}
