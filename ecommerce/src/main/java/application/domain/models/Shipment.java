package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.ShipmentStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/** Logistic process of one order from one warehouse: PENDING, PREPARING, SHIPPED, DELIVERED. */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shipment {
    @EqualsAndHashCode.Include
    private final Long shipmentId;
    private final Long orderId;
    private final Long warehouseId;
    private ShipmentStatus status;
    private final Address deliveryAddress;

    public Shipment(Long shipmentId, Long orderId, Long warehouseId,
                    ShipmentStatus status, Address deliveryAddress) {
        this.shipmentId = Objects.requireNonNull(shipmentId, "Shipment ID is required.");
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required.");
        this.warehouseId = Objects.requireNonNull(warehouseId, "Warehouse ID is required.");
        this.status = Objects.requireNonNull(status, "Shipment status is required.");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "Delivery address is required.");
    }

    public void prepare() {
        transition(ShipmentStatus.PENDING, ShipmentStatus.PREPARING);
    }

    public void dispatch() {
        transition(ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED);
    }

    public void confirmDelivery() {
        transition(ShipmentStatus.SHIPPED, ShipmentStatus.DELIVERED);
    }

    private void transition(ShipmentStatus expected, ShipmentStatus next) {
        if (status != expected) {
            throw new BusinessRuleViolationException("Invalid shipment status transition: " + status + " -> " + next);
        }
        status = next;
    }
}
