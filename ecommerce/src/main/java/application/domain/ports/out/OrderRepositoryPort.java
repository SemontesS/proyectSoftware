package application.domain.ports.out;

import application.domain.models.Order;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepositoryPort {
    Long nextId();

    Order save(Order order);

    Optional<Order> findById(Long orderId);

    List<Order> findByBuyerId(Long buyerId);

    Map<OrderStatus, Long> countByStatus();

    /** Sum of the totals of paid orders (PAID or later, excluding cancelled), per currency. */
    Map<Currency, Money> salesTotalsByCurrency();
}
