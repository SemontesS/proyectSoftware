package application.domain.ports.out;

import application.domain.models.Cart;

import java.util.Optional;

public interface CartRepositoryPort {
    Long nextId();

    Cart save(Cart cart);

    Optional<Cart> findActiveByBuyerId(Long buyerId);
}
