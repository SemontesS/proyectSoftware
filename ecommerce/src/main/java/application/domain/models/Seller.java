package application.domain.models;

import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Seller extends User {
    private SellerStatus sellerStatus;

    public Seller(Long userId, String name, String email, String identityDocument,
                  UserStatus status, SellerStatus sellerStatus) {
        super(userId, name, email, identityDocument, UserRole.SELLER, status);
        this.sellerStatus = Objects.requireNonNull(sellerStatus, "Seller status is required.");
    }

    public void changeSellerStatus(SellerStatus status) {
        sellerStatus = Objects.requireNonNull(status, "Seller status is required.");
    }
}
