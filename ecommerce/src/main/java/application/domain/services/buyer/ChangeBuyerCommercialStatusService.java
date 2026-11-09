package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Enables or restricts the commercial capability (purchasing) of a buyer. Only administrators. */
@Service
@RequiredArgsConstructor
public class ChangeBuyerCommercialStatusService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Buyer change(User requestingUser, Long buyerId, BuyerStatus status) {
        permissions.validate(requestingUser, Permission.BUYER_CHANGE_COMMERCIAL_STATUS);
        Buyer buyer = userRepositoryPort.findBuyerById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer", buyerId));
        buyer.changeCommercialStatus(status);
        return userRepositoryPort.save(buyer);
    }
}
