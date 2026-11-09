package application.domain.services.seller;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Seller;
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
public class ConsultSellerService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Seller consult(User requestingUser, Long sellerId) {
        permissions.validate(requestingUser, Permission.SELLER_CONSULT);
        Seller seller = userRepositoryPort.findSellerById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller", sellerId));
        resourceAccess.validateOwnerOrRole(requestingUser, seller.getUserId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return seller;
    }
}
