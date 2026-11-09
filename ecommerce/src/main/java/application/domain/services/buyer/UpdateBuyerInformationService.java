package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.models.User;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Changes the primary address and/or adds additional addresses. Null arguments mean "no change". */
@Service
@RequiredArgsConstructor
public class UpdateBuyerInformationService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Buyer update(User requestingUser, Long buyerId, Address newPrimaryAddress, List<Address> addressesToAdd) {
        permissions.validate(requestingUser, Permission.BUYER_UPDATE);
        boolean nothingToAdd = addressesToAdd == null || addressesToAdd.isEmpty();
        if (newPrimaryAddress == null && nothingToAdd) throw new IllegalArgumentException("Nothing to update.");
        Buyer buyer = userRepositoryPort.findBuyerById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer", buyerId));
        resourceAccess.validateOwnerOrRole(requestingUser, buyer.getUserId(), UserRole.ADMINISTRATOR);

        if (newPrimaryAddress != null) buyer.changePrimaryAddress(newPrimaryAddress);
        if (!nothingToAdd) addressesToAdd.forEach(buyer::addAdditionalAddress);
        return userRepositoryPort.save(buyer);
    }
}
