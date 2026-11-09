package application.domain.ports.out;

import application.domain.models.Shipment;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepositoryPort {
    Long nextId();

    Shipment save(Shipment shipment);

    Optional<Shipment> findById(Long shipmentId);

    List<Shipment> findByOrderId(Long orderId);
}
