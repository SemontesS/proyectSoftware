package application.domain.services.seller;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.SellerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Activates or suspends a seller. A suspended seller cannot publish products nor sell (see CreateOrderService). */
@Service
@RequiredArgsConstructor
public class ChangeSellerStatusService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Seller change(User requestingUser, Long sellerId, SellerStatus status) {
        permissions.validate(requestingUser, Permission.SELLER_CHANGE_STATUS);
        Seller seller = userRepositoryPort.findSellerById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller", sellerId));
        seller.changeSellerStatus(status);
        return userRepositoryPort.save(seller);
    }
}
