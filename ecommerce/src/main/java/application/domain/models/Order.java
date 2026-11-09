package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    @EqualsAndHashCode.Include
    private final Long orderId;
    private final Long buyerId;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    @Getter(AccessLevel.NONE)
    private final List<OrderLine> lines = new ArrayList<>();
    private final Currency currency;

    public Order(Long orderId, Long buyerId, LocalDateTime createdAt, Currency currency) {
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required.");
        this.buyerId = Objects.requireNonNull(buyerId, "Buyer ID is required.");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        this.currency = Objects.requireNonNull(currency, "Currency is required.");
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public void addLine(OrderLine line) {
        validateModifiable();
        Objects.requireNonNull(line, "Order line is required.");
        if (currency != line.getUnitPrice().currency()) {
            throw new IllegalArgumentException("Order line currency must match order currency.");
        }
        lines.add(line);
    }

    public void confirmPayment() {
        transitionTo(OrderStatus.PAID);
    }

    public void ship() {
        transitionTo(OrderStatus.SHIPPED);
    }

    public void deliver() {
        transitionTo(OrderStatus.DELIVERED);
    }

    public void complete() {
        transitionTo(OrderStatus.COMPLETED);
    }

    public void cancel() {
        if (status != OrderStatus.PENDING_PAYMENT && status != OrderStatus.PAID) {
            throw new BusinessRuleViolationException("Only an order before dispatch can be cancelled.");
        }
        transitionTo(OrderStatus.CANCELLED);
    }

    /** Lines with a warehouse are physical goods; digital lines have no warehouse. */
    public boolean hasPhysicalLines() {
        return lines.stream().anyMatch(line -> line.getWarehouseId() != null);
    }

    public boolean canBeModified() {
        return status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED;
    }

    public Money getTotal() {
        return lines.stream().map(OrderLine::getSubtotal).reduce(Money.zero(currency), Money::add);
    }

    public List<OrderLine> getLines() {
        return List.copyOf(lines);
    }

    private void transitionTo(OrderStatus newStatus) {
        validateModifiable();
        Objects.requireNonNull(newStatus, "Order status is required.");
        boolean valid = switch (status) {
            case PENDING_PAYMENT -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED
                    || newStatus == OrderStatus.COMPLETED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED -> newStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
        if (!valid) throw new BusinessRuleViolationException("Invalid order status transition: " + status + " -> " + newStatus);
        status = newStatus;
    }

    private void validateModifiable() {
        if (!canBeModified()) throw new BusinessRuleViolationException("A completed or cancelled order cannot be modified.");
    }
}
