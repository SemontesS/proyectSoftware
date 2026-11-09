package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateWarehouseService {
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Warehouse update(User requestingUser, Long warehouseId, Address location) {
        permissions.validate(requestingUser, Permission.WAREHOUSE_UPDATE);
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
        warehouse.changeLocation(location);
        return warehouseRepositoryPort.save(warehouse);
    }
}
