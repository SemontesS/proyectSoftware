package application.domain.models;

import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Buyer extends User {
    private Address primaryAddress;
    @Getter(AccessLevel.NONE)
    private final List<Address> additionalAddresses = new ArrayList<>();
    private BuyerStatus commercialStatus;

    public Buyer(Long userId, String name, String email, String identityDocument,
                 UserStatus status, Address primaryAddress, BuyerStatus commercialStatus) {
        super(userId, name, email,  identityDocument, UserRole.BUYER, status);
        this.primaryAddress = Objects.requireNonNull(primaryAddress, "Primary address is required.");
        this.commercialStatus = Objects.requireNonNull(commercialStatus, "Commercial status is required.");
    }

    public void addAdditionalAddress(Address address) {
        additionalAddresses.add(Objects.requireNonNull(address, "Address is required."));
    }

    public void changePrimaryAddress(Address address) {
        primaryAddress = Objects.requireNonNull(address, "Primary address is required.");
    }

    public void changeCommercialStatus(BuyerStatus status) {
        commercialStatus = Objects.requireNonNull(status, "Commercial status is required.");
    }

    public List<Address> getAdditionalAddresses() {
        return List.copyOf(additionalAddresses);
    }
}
