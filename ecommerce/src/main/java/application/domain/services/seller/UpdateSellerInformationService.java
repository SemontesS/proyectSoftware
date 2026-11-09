package application.domain.services.seller;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Administrator-managed seller data (name, email). Null arguments mean "no change". */
@Service
@RequiredArgsConstructor
public class UpdateSellerInformationService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Seller update(User requestingUser, Long sellerId, String name, Email email) {
        permissions.validate(requestingUser, Permission.SELLER_UPDATE);
        if (name == null && email == null) throw new IllegalArgumentException("Nothing to update.");
        Seller seller = userRepositoryPort.findSellerById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller", sellerId));
        if (email != null && !email.equals(seller.getEmail())) {
            if (userRepositoryPort.existsByEmail(email)) {
                throw new DuplicateEntityException("The email is already registered: " + email.value());
            }
            seller.changeEmail(email);
        }
        if (name != null) seller.changeName(name);
        return userRepositoryPort.save(seller);
    }
}
