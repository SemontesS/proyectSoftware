package application.domain.models;

import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserRole;

import java.util.Objects;

/**
 * Seller represents al participant responsible de record y manage
 * los products que comercializa dentro de NexusMarket.
 *
 * Rules (Domain Model — NexusMarket, sección 6 / 21):
 * - Must ser incorporado por el Administrador (no puede auto-recordse;
 *   la creación se orquesta desde el caso de uso correspondiente, no desde
 *   esta entidad).
 * - Can record y manage products.
 * - Can trabajar with las warehouses associated.
 */
public class Seller extends User {

    private SellerStatus sellerStatus;

    public Seller(Long userId, String name, String email,
                     String identityDocument, UserStatus status,
                     SellerStatus sellerStatus) {
        super(userId, name, email, identityDocument, UserRole.SELLER, status);
        this.sellerStatus = Objects.requireNonNull(sellerStatus, "sellerStatus es obligatorio");
    }

    public void changeSellerStatus(SellerStatus sellerStatus) {
        this.sellerStatus = Objects.requireNonNull(sellerStatus);
    }

    public SellerStatus getSellerStatus() {
        return sellerStatus;
    }
}
