package application.domain.valueobjects;

/** Values are assumptions and may require business validation. */
public enum RefundStatus {
    PENDING,
    APPROVED,
    PAID,
    REJECTED
}
