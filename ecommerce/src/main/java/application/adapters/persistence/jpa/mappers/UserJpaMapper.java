package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        Email email = new Email(entity.getEmail());
        IdentityDocument document = new IdentityDocument(entity.getIdentityDocument());
        UserRole role = UserRole.valueOf(entity.getRole());
        UserStatus status = UserStatus.valueOf(entity.getStatus());
        return switch (role) {
            case BUYER -> {
                Buyer buyer = new Buyer(entity.getId(), entity.getName(), email, document, status,
                        JpaCommonMapper.toDomain(entity.getPrimaryAddress()),
                        BuyerStatus.valueOf(entity.getCommercialStatus()));
                entity.getAdditionalAddresses().forEach(a -> buyer.addAdditionalAddress(JpaCommonMapper.toDomain(a)));
                yield buyer;
            }
            case SELLER -> new Seller(entity.getId(), entity.getName(), email, document, status,
                    SellerStatus.valueOf(entity.getSellerStatus()));
            default -> new InternalUser(entity.getId(), entity.getName(), email, document, role, status);
        };
    }

    public static void updateEntity(UserJpaEntity entity, User user) {
        entity.setId(user.getUserId());
        entity.setRole(user.getRole().name());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail().value());
        entity.setIdentityDocument(user.getIdentityDocument().value());
        entity.setStatus(user.getStatus().name());
        if (user instanceof Buyer buyer) {
            entity.setCommercialStatus(buyer.getCommercialStatus().name());
            entity.setPrimaryAddress(JpaCommonMapper.toEmbeddable(buyer.getPrimaryAddress()));
            entity.getAdditionalAddresses().clear();
            buyer.getAdditionalAddresses().forEach(a -> entity.getAdditionalAddresses().add(JpaCommonMapper.toEmbeddable(a)));
        }
        if (user instanceof Seller seller) {
            entity.setSellerStatus(seller.getSellerStatus().name());
        }
    }
}
