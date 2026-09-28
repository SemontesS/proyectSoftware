package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order es un Aggregate Root (Domain Model — NexusMarket, sección 20).
 * Represents el compromiso commercial generado después de que el buyer
 * confirma su cart.
 *
 * Rule: un order en status COMPLETED no puede modificarse
 * (Domain Model — NexusMarket, sección 14 / 21).
 */
public class Order {

    private final Long orderId;
    private final Long buyerId;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private final List<OrderLine> lines = new ArrayList<>();
    private final String currency;

    public static Order confirmFromCart(Long orderId, Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede generar un order de un cart vacío.");
        }
        Order order = new Order(orderId, cart.getBuyerId(), LocalDateTime.now(),
                cart.getTotal().currency());
        for (CartItem item : cart.getItems()) {
            order.lines.add(new OrderLine(item.getProductId(), item.getCantidad(), item.getUnitPrice()));
        }
        return order;
    }

    public Order(Long orderId, Long buyerId, LocalDateTime createdAt, String currency) {
        this.orderId = Objects.requireNonNull(orderId, "orderId es obligatorio");
        this.buyerId = Objects.requireNonNull(buyerId, "buyerId es obligatorio");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        this.status = OrderStatus.PENDIENTE_PAGO;
        this.currency = Objects.requireNonNull(currency, "currency es obligatoria");
    }

    public void confirmPayment() {
        changeStatus(OrderStatus.PAID);
    }

    public void ship() {
        changeStatus(OrderStatus.SHIPPED);
    }

    public void deliver() {
        changeStatus(OrderStatus.DELIVERED);
    }

    public void complete() {
        changeStatus(OrderStatus.COMPLETED);
    }

    private void changeStatus(OrderStatus newStatus) {
        validateModifiable();
        this.status = Objects.requireNonNull(newStatus);
    }

    public boolean canBeModified() {
        return status != OrderStatus.COMPLETED;
    }

    private void validateModifiable() {
        if (!canBeModified()) {
            throw new IllegalStateException("A order finalizado no puede modificarse.");
        }
    }

    public Money getTotal() {
        return lines.stream()
                .map(OrderLine::getSubtotal)
                .reduce(Money.of(0, currency), Money::add);
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return List.copyOf(lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
