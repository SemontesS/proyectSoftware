package application.domain.models;

import application.domain.valueobjects.Address;
import application.domain.valueobjects.ShipmentStatus;

import java.util.Objects;

/**
 * Shipment represents el proceso logístico mediante el cual un order es
 * preparado, despachado, transportado y entregado (Domain Model —
 * NexusMarket, sección 17). A order puede requerir zero o un shipment; una
 * warehouse puede ship zero o más envíos.
 */
public class Shipment {

    private final Long shipmentId;
    private final Long orderId;
    private final Long warehouseId;
    private ShipmentStatus status;
    private final Address deliveryAddress;

    public Shipment(Long shipmentId, Long orderId, Long warehouseId, ShipmentStatus status, Address deliveryAddress) {
        this.shipmentId = Objects.requireNonNull(shipmentId, "shipmentId es obligatorio");
        this.orderId = Objects.requireNonNull(orderId, "orderId es obligatorio");
        this.warehouseId = Objects.requireNonNull(warehouseId, "warehouseId es obligatorio");
        this.status = Objects.requireNonNull(status, "status es obligatorio");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress es obligatoria");
    }

    public void changeStatus(ShipmentStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus);
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shipment shipment)) return false;
        return Objects.equals(shipmentId, shipment.shipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipmentId);
    }
}
