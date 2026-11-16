package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Registers that the returned goods arrived (REQUESTED -> RECEIVED). If the line is physical, its
 * units go back to the warehouse that shipped them (RETURN movement); digital lines have no stock.
 * Only the returned line is touched.
 */
@Service
@RequiredArgsConstructor
public class ReceiveReturnService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Return receive(User requestingUser, Long returnId) {
        permissions.validate(requestingUser, Permission.RETURN_RECEIVE);
        Return returnRequest = returnRepositoryPort.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
        returnRequest.receive();
        Order order = orderRepositoryPort.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", returnRequest.getOrderId()));
        order.getLines().stream()
                .filter(line -> line.getProductId().equals(returnRequest.getProductId())
                        && line.getSku().equals(returnRequest.getSku()))
                .filter(line -> line.getWarehouseId() != null)
                .findFirst()
                .ifPresent(line -> restock(line, returnRequest));
        return returnRepositoryPort.save(returnRequest);
    }

    private void restock(OrderLine line, Return returnRequest) {
        Inventory inventory = inventoryRepositoryPort
                .findByProductSkuAndWarehouse(line.getProductId(), line.getSku(), line.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Inventory", line.getProductId()));
        inventory.recordReturn(returnRequest.getQuantity());
        inventoryRepositoryPort.save(inventory);
    }
}
