package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.UserRole;

import java.util.Map;
import java.util.Optional;

public interface UserRepositoryPort {
    Long nextId();

    <T extends User> T save(T user);

    Optional<User> findById(Long userId);

    Optional<Buyer> findBuyerById(Long userId);

    Optional<Seller> findSellerById(Long userId);

    boolean existsByEmail(Email email);

    boolean existsByIdentityDocument(IdentityDocument identityDocument);

    Map<UserRole, Long> countByRole();
}
