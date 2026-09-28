package application.domain.models;

import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Buyer represents al participant que adquiere products dentro de
 * NexusMarket.
 *
 * Rules (Domain Model — NexusMarket, sección 5 / 21):
 * - Must contar with una address principal.
 * - Can record addresses additional.
 * - Can utilizar un cart activo (zero o uno).
 * - Can realizar orders.
 */
public class Buyer extends User {

    private Address primaryAddress;
    private final List<Address> additionalAddresses = new ArrayList<>();
    private BuyerStatus commercialStatus;

    public Buyer(Long userId, String name, String email,
                      String identityDocument, UserStatus status,
                      Address primaryAddress, BuyerStatus commercialStatus) {
        super(userId, name, email, identityDocument, UserRole.BUYER, status);
        this.primaryAddress = Objects.requireNonNull(primaryAddress, "primaryAddress es obligatoria");
        this.commercialStatus = Objects.requireNonNull(commercialStatus, "commercialStatus es obligatorio");
    }

    public void addAdditionalAddress(Address address) {
        additionalAddresses.add(Objects.requireNonNull(address));
    }

    public void changePrimaryAddress(Address address) {
        this.primaryAddress = Objects.requireNonNull(address);
    }

    public void changeCommercialStatus(BuyerStatus commercialStatus) {
        this.commercialStatus = Objects.requireNonNull(commercialStatus);
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public List<Address> getAdditionalAddresses() {
        return List.copyOf(additionalAddresses);
    }

    public BuyerStatus getCommercialStatus() {
        return commercialStatus;
    }
}
