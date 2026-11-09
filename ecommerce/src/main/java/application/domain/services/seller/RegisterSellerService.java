package application.domain.services.seller;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.models.Seller;
import application.domain.models.SellerRegistration;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.valueobjects.RawPassword;
import application.domain.models.Warehouse;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Administrative onboarding of a seller together with its first warehouse. Only administrators. */
@Service
@RequiredArgsConstructor
public class RegisterSellerService {
    private final UserRepositoryPort userRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final CredentialsRepositoryPort credentialsRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final ValidateUserPermissionsService permissions;

    public SellerRegistration register(User requestingUser, String name, Email email,
                           IdentityDocument identityDocument, Address firstWarehouseLocation,
                           RawPassword password) {
        permissions.validate(requestingUser, Permission.SELLER_REGISTER);
        if (userRepositoryPort.existsByEmail(email)) {
            throw new DuplicateEntityException("The email is already registered: " + email.value());
        }
        if (userRepositoryPort.existsByIdentityDocument(identityDocument)) {
            throw new DuplicateEntityException("The identity document is already registered.");
        }
        Seller seller = userRepositoryPort.save(new Seller(userRepositoryPort.nextId(), name, email,
                identityDocument, UserStatus.ACTIVE, SellerStatus.ACTIVE));
        credentialsRepositoryPort.save(UserCredentials.create(seller.getUserId(), passwordServicePort.encrypt(password)));
        Warehouse warehouse = warehouseRepositoryPort.save(new Warehouse(warehouseRepositoryPort.nextId(),
                seller.getUserId(), firstWarehouseLocation, WarehouseType.SELLER, WarehouseStatus.ACTIVE));
        return new SellerRegistration(seller, warehouse);
    }
}
