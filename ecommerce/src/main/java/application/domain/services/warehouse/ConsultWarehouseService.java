package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Staff roles consult any warehouse; a seller only its own. */
@Service
@RequiredArgsConstructor
public class ConsultWarehouseService {
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Warehouse consult(User requestingUser, Long warehouseId) {
        permissions.validate(requestingUser, Permission.WAREHOUSE_CONSULT);
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
        resourceAccess.validateOwnerOrRole(requestingUser, warehouse.getSellerId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR, UserRole.LOGISTICS_OPERATOR);
        return warehouse;
    }
}
