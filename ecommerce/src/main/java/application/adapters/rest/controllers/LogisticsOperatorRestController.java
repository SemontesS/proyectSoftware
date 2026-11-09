package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.AdjustmentRequestDTO;
import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.QuantityRequestDTO;
import application.adapters.rest.dtos.requests.RegisterInitialInventoryRequestDTO;
import application.adapters.rest.dtos.requests.ReserveInventoryRequestDTO;
import application.adapters.rest.dtos.requests.UpdateUserRequestDTO;
import application.adapters.rest.dtos.responses.InventoryAllocationResponseDTO;
import application.adapters.rest.dtos.responses.InventoryResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.ReturnResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.CommonRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
public class LogisticsOperatorRestController {
    private final LogisticsOperatorPort logisticsPort;

    // ---- Account ----
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> user(@AuthenticationPrincipal(expression = "user") User user,
                                                @PathVariable Long userId) {
        return ResponseEntity.ok(UserRestMapper.toDTO(logisticsPort.consultUser(user, userId)));
    }

    @PutMapping("/account")
    public ResponseEntity<UserResponseDTO> updateAccount(@AuthenticationPrincipal(expression = "user") User user,
                                                         @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toDTO(logisticsPort.updateUser(user, user.getUserId(),
                request.name(), CommonRestMapper.toEmailOrNull(request.email()))));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        logisticsPort.changePassword(user, new RawPassword(request.currentPassword()), new RawPassword(request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    // ---- Reference data ----
    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> warehouse(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long warehouseId) {
        return ResponseEntity.ok(WarehouseRestMapper.toDTO(logisticsPort.consultWarehouse(user, warehouseId)));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> catalog(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ProductRestMapper.toDTOs(logisticsPort.consultCatalog(user)));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> product(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(logisticsPort.consultProduct(user, productId)));
    }

    // ---- Inventory ----
    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponseDTO> registerInitialInventory(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterInitialInventoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryRestMapper.toDTO(
                logisticsPort.registerInitialInventory(user, request.productId(), new Sku(request.sku()),
                        request.warehouseId(), request.initialQuantity())));
    }

    @PostMapping("/inventory/{inventoryId}/entries")
    public ResponseEntity<InventoryResponseDTO> registerEntry(@AuthenticationPrincipal(expression = "user") User user,
                                                              @PathVariable Long inventoryId,
                                                              @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                logisticsPort.registerInventoryEntry(user, inventoryId, request.quantity())));
    }

    @PostMapping("/inventory/reservations")
    public ResponseEntity<List<InventoryAllocationResponseDTO>> reserve(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody ReserveInventoryRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toAllocationDTOs(logisticsPort.reserveInventory(
                user, request.productId(), new Sku(request.sku()), request.quantity())));
    }

    @PostMapping("/inventory/{inventoryId}/sale-dispatches")
    public ResponseEntity<InventoryResponseDTO> registerSaleDispatch(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                logisticsPort.registerSaleDispatch(user, inventoryId, request.quantity())));
    }

    @PostMapping("/inventory/{inventoryId}/adjustments")
    public ResponseEntity<InventoryResponseDTO> adjust(@AuthenticationPrincipal(expression = "user") User user,
                                                       @PathVariable Long inventoryId,
                                                       @Valid @RequestBody AdjustmentRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                logisticsPort.adjustInventory(user, inventoryId, request.delta())));
    }

    @PostMapping("/inventory/{inventoryId}/returns")
    public ResponseEntity<InventoryResponseDTO> registerInventoryReturn(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                logisticsPort.registerInventoryReturn(user, inventoryId, request.quantity())));
    }

    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<List<InventoryResponseDTO>> inventory(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long productId,
                                                                @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(InventoryRestMapper.toDTOs(logisticsPort.consultInventory(user, productId, warehouseId)));
    }

    // ---- Orders and shipments ----
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> order(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(logisticsPort.consultOrder(user, orderId)));
    }

    @PostMapping("/orders/{orderId}/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> prepareShipments(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderRestMapper.toShipmentDTOs(logisticsPort.prepareShipment(user, orderId)));
    }

    @GetMapping("/orders/{orderId}/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> shipmentsOfOrder(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTOs(logisticsPort.consultShipmentsByOrder(user, orderId)));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ShipmentResponseDTO> shipment(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(logisticsPort.consultShipment(user, shipmentId)));
    }

    @PostMapping("/shipments/{shipmentId}/dispatch")
    public ResponseEntity<ShipmentResponseDTO> dispatch(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(logisticsPort.dispatchShipment(user, shipmentId)));
    }

    @PostMapping("/shipments/{shipmentId}/delivery")
    public ResponseEntity<ShipmentResponseDTO> confirmDelivery(@AuthenticationPrincipal(expression = "user") User user,
                                                               @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(logisticsPort.confirmDelivery(user, shipmentId)));
    }

    // ---- Returns ----
    @PostMapping("/returns/{returnId}/receive")
    public ResponseEntity<ReturnResponseDTO> receiveReturn(@AuthenticationPrincipal(expression = "user") User user,
                                                           @PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRestMapper.toReturnDTO(logisticsPort.receiveReturn(user, returnId)));
    }
}
