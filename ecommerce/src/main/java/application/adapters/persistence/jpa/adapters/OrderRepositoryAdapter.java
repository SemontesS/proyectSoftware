package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.mappers.*;
import application.adapters.persistence.jpa.repositories.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("orders");
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = repository.findById(order.getOrderId()).orElseGet(OrderJpaEntity::new);
        OrderJpaMapper.updateEntity(entity, order);
        repository.save(entity);
        return order;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return repository.findById(orderId).map(OrderJpaMapper::toDomain);
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        return repository.findByBuyerIdOrderByIdAsc(buyerId).stream().map(OrderJpaMapper::toDomain).toList();
    }

    @Override
    public Map<OrderStatus, Long> countByStatus() {
        Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
        for (Object[] row : repository.countGroupedByStatus()) {
            counts.put(OrderStatus.valueOf((String) row[0]), (Long) row[1]);
        }
        return counts;
    }

    @Override
    public Map<Currency, Money> salesTotalsByCurrency() {
        Map<Currency, Money> totals = new EnumMap<>(Currency.class);
        for (Object[] row : repository.salesTotalsGroupedByCurrency()) {
            Currency currency = Currency.valueOf((String) row[0]);
            totals.put(currency, Money.of((java.math.BigDecimal) row[1], currency));
        }
        return totals;
    }
}
