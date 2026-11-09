package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/** Internal element of the Return aggregate: its state is changed only through Return. */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Refund {
    @EqualsAndHashCode.Include
    private final Long refundId;
    private final Long returnId;
    private final Money amount;
    private final LocalDate date;
    private RefundStatus status;

    public Refund(Long refundId, Long returnId, Money amount, LocalDate date, RefundStatus status) {
        this.refundId = Objects.requireNonNull(refundId, "Refund ID is required.");
        this.returnId = Objects.requireNonNull(returnId, "Return ID is required.");
        this.amount = Objects.requireNonNull(amount, "Refund amount is required.");
        if (amount.amount().signum() <= 0) throw new IllegalArgumentException("Refund amount must be greater than zero.");
        this.date = Objects.requireNonNull(date, "Refund date is required.");
        this.status = Objects.requireNonNull(status, "Refund status is required.");
    }

    void approve() {
        transitionFromPending(RefundStatus.APPROVED);
    }

    void reject() {
        transitionFromPending(RefundStatus.REJECTED);
    }

    private void transitionFromPending(RefundStatus next) {
        if (status != RefundStatus.PENDING) {
            throw new BusinessRuleViolationException("Invalid refund status transition: " + status + " -> " + next);
        }
        status = next;
    }
}
