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
public class CartRepositoryAdapter implements CartRepositoryPort {
    private final CartJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("carts");
    }

    @Override
    public Cart save(Cart cart) {
        CartJpaEntity entity = repository.findById(cart.getCartId()).orElseGet(CartJpaEntity::new);
        CartJpaMapper.updateEntity(entity, cart);
        repository.save(entity);
        return cart;
    }

    @Override
    public Optional<Cart> findActiveByBuyerId(Long buyerId) {
        return repository.findFirstByBuyerIdAndStatus(buyerId, CartStatus.ACTIVE.name()).map(CartJpaMapper::toDomain);
    }
}
