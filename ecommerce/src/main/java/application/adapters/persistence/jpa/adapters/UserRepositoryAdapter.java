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
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("users");
    }

    @Override
    public <T extends User> T save(T user) {
        UserJpaEntity entity = repository.findById(user.getUserId()).orElseGet(UserJpaEntity::new);
        UserJpaMapper.updateEntity(entity, user);
        repository.save(entity);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return repository.findById(userId).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<Buyer> findBuyerById(Long userId) {
        return findById(userId).filter(Buyer.class::isInstance).map(Buyer.class::cast);
    }

    @Override
    public Optional<Seller> findSellerById(Long userId) {
        return findById(userId).filter(Seller.class::isInstance).map(Seller.class::cast);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByIdentityDocument(IdentityDocument identityDocument) {
        return repository.existsByIdentityDocument(identityDocument.value());
    }

    @Override
    public Map<UserRole, Long> countByRole() {
        Map<UserRole, Long> counts = new EnumMap<>(UserRole.class);
        for (Object[] row : repository.countGroupedByRole()) {
            counts.put(UserRole.valueOf((String) row[0]), (Long) row[1]);
        }
        return counts;
    }
}
