package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultBuyerService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Buyer consult(User requestingUser, Long buyerId) {
        permissions.validate(requestingUser, Permission.BUYER_CONSULT);
        Buyer buyer = userRepositoryPort.findBuyerById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer", buyerId));
        resourceAccess.validateOwnerOrRole(requestingUser, buyer.getUserId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return buyer;
    }
}
