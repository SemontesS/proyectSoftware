package application.domain.services.warehouse;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registers a marketplace warehouse (no seller) or a seller warehouse (active seller). Only administrators. */
@Service
@RequiredArgsConstructor
public class RegisterWarehouseService {
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Warehouse register(User requestingUser, WarehouseType type, Long sellerId, Address location) {
        permissions.validate(requestingUser, Permission.WAREHOUSE_REGISTER);
        if (type == WarehouseType.SELLER) {
            if (sellerId == null) throw new IllegalArgumentException("A seller warehouse must reference a seller.");
            Seller seller = userRepositoryPort.findSellerById(sellerId)
                    .orElseThrow(() -> new EntityNotFoundException("Seller", sellerId));
            if (seller.getSellerStatus() != SellerStatus.ACTIVE) {
                throw new BusinessRuleViolationException("A warehouse cannot be registered for a suspended seller.");
            }
        }
        Warehouse warehouse = new Warehouse(warehouseRepositoryPort.nextId(), sellerId, location,
                type, WarehouseStatus.ACTIVE);
        return warehouseRepositoryPort.save(warehouse);
    }
}
