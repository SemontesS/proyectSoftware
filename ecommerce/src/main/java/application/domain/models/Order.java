package application.domain.models;

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
    private final String currency;

    public static Order confirmFromCart(Long orderId, Cart cart) {
        Objects.requireNonNull(cart, "Cart is required.");
        if (cart.getItems().isEmpty()) throw new IllegalStateException("An empty cart cannot create an order.");
        Order order = new Order(orderId, cart.getBuyerId(), LocalDateTime.now(), cart.getTotal().currency());
        for (CartItem item : cart.getItems()) {
            order.addLine(new OrderLine(item.getProductId(), item.getSku(), null, null,
                    item.getQuantity(), item.getUnitPrice()));
        }
        return order;
    }

    public Order(Long orderId, Long buyerId, LocalDateTime createdAt, String currency) {
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required.");
        this.buyerId = Objects.requireNonNull(buyerId, "Buyer ID is required.");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("Currency is required.");
        this.currency = currency.trim().toUpperCase();
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public void addLine(OrderLine line) {
        validateModifiable();
        Objects.requireNonNull(line, "Order line is required.");
        if (!currency.equals(line.getUnitPrice().currency())) {
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
            throw new IllegalStateException("Only an order before dispatch can be cancelled.");
        }
        transitionTo(OrderStatus.CANCELLED);
    }

    public boolean canBeModified() {
        return status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED;
    }

    public Money getTotal() {
        return lines.stream().map(OrderLine::getSubtotal).reduce(Money.of(0, currency), Money::add);
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
        if (!valid) throw new IllegalStateException("Invalid order status transition: " + status + " -> " + newStatus);
        status = newStatus;
    }

    private void validateModifiable() {
        if (!canBeModified()) throw new IllegalStateException("A completed or cancelled order cannot be modified.");
    }
}
