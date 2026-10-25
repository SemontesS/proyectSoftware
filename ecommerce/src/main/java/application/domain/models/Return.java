package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Return {
    @EqualsAndHashCode.Include
    private final Long returnId;
    private final Long orderId;
    private final String reason;
    private final LocalDate date;
    private ReturnStatus status;
    @Getter(AccessLevel.NONE)
    private Refund refund;

    public Return(Long returnId, Long orderId, String reason, LocalDate date, ReturnStatus status) {
        this.returnId = Objects.requireNonNull(returnId, "Return ID is required.");
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Return reason is required.");
        this.reason = reason.trim();
        this.date = Objects.requireNonNull(date, "Return date is required.");
        this.status = Objects.requireNonNull(status, "Return status is required.");
    }

    public void changeStatus(ReturnStatus status) {
        this.status = Objects.requireNonNull(status, "Return status is required.");
    }

    public Refund generateRefund(Long refundId, Money amount, LocalDate date, RefundStatus initialStatus) {
        if (refund != null) throw new IllegalStateException("A return can generate at most one refund.");
        refund = new Refund(refundId, returnId, amount, date, initialStatus);
        return refund;
    }

    public Optional<Refund> getRefund() {
        return Optional.ofNullable(refund);
    }
}
