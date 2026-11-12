package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class OrderJpaMapper {

    private OrderJpaMapper() {
    }

    public static Order toDomain(OrderJpaEntity entity) {
        List<OrderLine> lines = entity.getLines().stream()
                .map(l -> new OrderLine(l.getProductId(), new Sku(l.getSku()), l.getSellerId(), l.getWarehouseId(),
                        new Quantity(l.getQuantity()), JpaCommonMapper.toMoney(l.getUnitAmount(), l.getUnitCurrency())))
                .toList();
        return Order.restore(entity.getId(), entity.getBuyerId(), entity.getCreatedAt(),
                OrderStatus.valueOf(entity.getStatus()), Currency.valueOf(entity.getCurrency()), lines);
    }

    public static void updateEntity(OrderJpaEntity entity, Order order) {
        entity.setId(order.getOrderId());
        entity.setBuyerId(order.getBuyerId());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setStatus(order.getStatus().name());
        entity.setCurrency(order.getCurrency().name());
        entity.getLines().clear();
        order.getLines().forEach(l -> entity.getLines().add(new OrderLineEmbeddable(l.getProductId(),
                l.getSku().value(), l.getSellerId(), l.getWarehouseId(), l.getQuantity().value(),
                l.getUnitPrice().amount(), l.getUnitPrice().currency().name())));
    }
}
