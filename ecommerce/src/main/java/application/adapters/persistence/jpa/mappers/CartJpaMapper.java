package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class CartJpaMapper {

    private CartJpaMapper() {
    }

    public static Cart toDomain(CartJpaEntity entity) {
        List<CartItem> items = entity.getItems().stream()
                .map(i -> new CartItem(i.getProductId(), new Sku(i.getSku()), new Quantity(i.getQuantity()),
                        JpaCommonMapper.toMoney(i.getUnitAmount(), i.getUnitCurrency())))
                .toList();
        return Cart.restore(entity.getId(), entity.getBuyerId(), entity.getCreatedAt(),
                CartStatus.valueOf(entity.getStatus()), Currency.valueOf(entity.getCurrency()), items);
    }

    public static void updateEntity(CartJpaEntity entity, Cart cart) {
        entity.setId(cart.getCartId());
        entity.setBuyerId(cart.getBuyerId());
        entity.setCreatedAt(cart.getCreatedAt());
        entity.setStatus(cart.getStatus().name());
        entity.setCurrency(cart.getCurrency().name());
        entity.getItems().clear();
        cart.getItems().forEach(i -> entity.getItems().add(new CartItemEmbeddable(i.getProductId(),
                i.getSku().value(), i.getQuantity().value(), i.getUnitPrice().amount(), i.getUnitPrice().currency().name())));
    }
}
