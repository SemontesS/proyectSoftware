package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.adapters.rest.dtos.responses.OrderLineResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Shipment;

import java.util.List;

/** Orders, invoices and shipments: the aggregates of the purchase flow. */
public final class OrderRestMapper {

    private OrderRestMapper() {
    }

    public static OrderResponseDTO toOrderDTO(Order order) {
        return new OrderResponseDTO(order.getOrderId(), order.getBuyerId(), order.getStatus().name(),
                order.getCreatedAt(), order.getCurrency().name(),
                order.getLines().stream().map(OrderRestMapper::toLineDTO).toList(),
                CommonRestMapper.toDTO(order.getTotal()));
    }

    public static List<OrderResponseDTO> toOrderDTOs(List<Order> orders) {
        return orders.stream().map(OrderRestMapper::toOrderDTO).toList();
    }

    private static OrderLineResponseDTO toLineDTO(OrderLine line) {
        return new OrderLineResponseDTO(line.getProductId(), line.getSku().value(), line.getSellerId(),
                line.getWarehouseId(), line.getQuantity(), CommonRestMapper.toDTO(line.getUnitPrice()),
                CommonRestMapper.toDTO(line.getSubtotal()));
    }

    public static InvoiceResponseDTO toInvoiceDTO(Invoice invoice) {
        return new InvoiceResponseDTO(invoice.getInvoiceId(), invoice.getOrderId(), invoice.getNumber().value(),
                invoice.getDate(), CommonRestMapper.toDTO(invoice.getSubtotal()),
                CommonRestMapper.toDTO(invoice.getTotal()));
    }

    public static ShipmentResponseDTO toShipmentDTO(Shipment shipment) {
        return new ShipmentResponseDTO(shipment.getShipmentId(), shipment.getOrderId(), shipment.getWarehouseId(),
                shipment.getStatus().name(), CommonRestMapper.toDTO(shipment.getDeliveryAddress()));
    }

    public static List<ShipmentResponseDTO> toShipmentDTOs(List<Shipment> shipments) {
        return shipments.stream().map(OrderRestMapper::toShipmentDTO).toList();
    }
}
