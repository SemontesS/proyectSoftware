package application.domain.models;

import application.domain.valueobjects.Address;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Warehouse {
    @EqualsAndHashCode.Include
    private final Long warehouseId;
    private final Long sellerId;
    private Address location;
    private final WarehouseType type;
    private WarehouseStatus status;

    public Warehouse(Long warehouseId, Long sellerId, Address location,
                     WarehouseType type, WarehouseStatus status) {
        this.warehouseId = Objects.requireNonNull(warehouseId, "Warehouse ID is required.");
        this.type = Objects.requireNonNull(type, "Warehouse type is required.");
        if (type == WarehouseType.SELLER && sellerId == null) {
            throw new IllegalArgumentException("A seller warehouse must reference a seller.");
        }
        if (type == WarehouseType.MARKETPLACE && sellerId != null) {
            throw new IllegalArgumentException("A marketplace warehouse cannot reference a seller.");
        }
        this.sellerId = sellerId;
        this.location = Objects.requireNonNull(location, "Warehouse location is required.");
        this.status = Objects.requireNonNull(status, "Warehouse status is required.");
    }

    public void changeLocation(Address location) {
        this.location = Objects.requireNonNull(location, "Warehouse location is required.");
    }

    public void changeStatus(WarehouseStatus status) {
        this.status = Objects.requireNonNull(status, "Warehouse status is required.");
    }
}
