package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Return of an order. Lifecycle: REQUESTED -> RECEIVED -> APPROVED | REJECTED, where the last
 * step is the outcome of the refund decision. A return generates at most one refund.
 */
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

    /** The returned goods arrived at the marketplace. */
    public void receive() {
        if (status != ReturnStatus.REQUESTED) {
            throw new BusinessRuleViolationException("Invalid return status transition: " + status + " -> RECEIVED");
        }
        status = ReturnStatus.RECEIVED;
    }

    public Refund generateRefund(Long refundId, Money amount, LocalDate date) {
        if (status != ReturnStatus.RECEIVED) {
            throw new BusinessRuleViolationException("A refund can only be generated for a received return.");
        }
        if (refund != null) throw new BusinessRuleViolationException("A return can generate at most one refund.");
        refund = new Refund(refundId, returnId, amount, date, RefundStatus.PENDING);
        return refund;
    }

    public Refund approveRefund() {
        Refund current = requireRefund();
        current.approve();
        status = ReturnStatus.APPROVED;
        return current;
    }

    public Refund rejectRefund() {
        Refund current = requireRefund();
        current.reject();
        status = ReturnStatus.REJECTED;
        return current;
    }

    public Optional<Refund> getRefund() {
        return Optional.ofNullable(refund);
    }

    private Refund requireRefund() {
        if (refund == null) throw new BusinessRuleViolationException("The return has no refund to decide.");
        return refund;
    }

    /** Rebuilds a persisted return with its optional refund. For persistence adapters only. */
    public static Return restore(Long returnId, Long orderId, String reason, LocalDate date,
                                 ReturnStatus status, Refund refund) {
        Return returnRequest = new Return(returnId, orderId, reason, date, status);
        returnRequest.refund = refund;
        return returnRequest;
    }
}
