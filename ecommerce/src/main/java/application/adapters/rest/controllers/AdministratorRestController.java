package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.AdjustmentRequestDTO;
import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.GenerateRefundRequestDTO;
import application.adapters.rest.dtos.requests.QuantityRequestDTO;
import application.adapters.rest.dtos.requests.RegisterInitialInventoryRequestDTO;
import application.adapters.rest.dtos.requests.RegisterInternalUserRequestDTO;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterWarehouseRequestDTO;
import application.adapters.rest.dtos.requests.ReserveInventoryRequestDTO;
import application.adapters.rest.dtos.requests.StatusRequestDTO;
import application.adapters.rest.dtos.requests.UpdateBuyerAddressesRequestDTO;
import application.adapters.rest.dtos.requests.UpdateUserRequestDTO;
import application.adapters.rest.dtos.requests.UpdateWarehouseRequestDTO;
import application.adapters.rest.dtos.responses.AdministrativeReportResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.InventoryAllocationResponseDTO;
import application.adapters.rest.dtos.responses.InventoryResponseDTO;
import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.RefundResponseDTO;
import application.adapters.rest.dtos.responses.ReturnResponseDTO;
import application.adapters.rest.dtos.responses.SellerRegistrationResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.CommonRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.ReportRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.AdministratorPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.Sku;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static application.adapters.rest.mappers.CommonRestMapper.enumOf;
import static application.adapters.rest.mappers.CommonRestMapper.toEmailOrNull;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdministratorRestController {
    private final AdministratorPort administratorPort;

    // ---- Users ----
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> registerInternalUser(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterInternalUserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toDTO(
                administratorPort.registerInternalUser(user, request.name(),
                        CommonRestMapper.toEmailOrNull(request.email()),
                        new IdentityDocument(request.identityDocument()),
                        enumOf(UserRole.class, request.role()), new RawPassword(request.password()))));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> user(@AuthenticationPrincipal(expression = "user") User user,
                                                @PathVariable Long userId) {
        return ResponseEntity.ok(UserRestMapper.toDTO(administratorPort.consultUser(user, userId)));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long userId,
                                                      @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toDTO(
                administratorPort.updateUser(user, userId, request.name(), toEmailOrNull(request.email()))));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserResponseDTO> changeUserStatus(@AuthenticationPrincipal(expression = "user") User user,
                                                            @PathVariable Long userId,
                                                            @Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toDTO(administratorPort.changeUserStatus(
                user, userId, enumOf(UserStatus.class, request.status()))));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        administratorPort.changePassword(user, new RawPassword(request.currentPassword()),
                new RawPassword(request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    // ---- Buyers ----
    @GetMapping("/buyers/{buyerId}")
    public ResponseEntity<BuyerResponseDTO> buyer(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long buyerId) {
        return ResponseEntity.ok(UserRestMapper.toBuyerDTO(administratorPort.consultBuyer(user, buyerId)));
    }

    @PutMapping("/buyers/{buyerId}/addresses")
    public ResponseEntity<BuyerResponseDTO> updateBuyerAddresses(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long buyerId,
            @Valid @RequestBody UpdateBuyerAddressesRequestDTO request) {
        Address primary = request.primaryAddress() == null ? null : CommonRestMapper.toDomain(request.primaryAddress());
        List<Address> additional = request.additionalAddresses() == null ? null
                : request.additionalAddresses().stream().map(CommonRestMapper::toDomain).toList();
        return ResponseEntity.ok(UserRestMapper.toBuyerDTO(
                administratorPort.updateBuyerInformation(user, buyerId, primary, additional)));
    }

    @PatchMapping("/buyers/{buyerId}/commercial-status")
    public ResponseEntity<BuyerResponseDTO> changeBuyerCommercialStatus(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long buyerId,
            @Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toBuyerDTO(administratorPort.changeBuyerCommercialStatus(
                user, buyerId, enumOf(BuyerStatus.class, request.status()))));
    }

    // ---- Sellers ----
    @PostMapping("/sellers")
    public ResponseEntity<SellerRegistrationResponseDTO> registerSeller(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterSellerRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toSellerRegistrationDTO(
                administratorPort.registerSeller(user, request.name(), CommonRestMapper.toEmailOrNull(request.email()),
                        new IdentityDocument(request.identityDocument()),
                        CommonRestMapper.toDomain(request.warehouseLocation()), new RawPassword(request.password()))));
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<SellerResponseDTO> seller(@AuthenticationPrincipal(expression = "user") User user,
                                                    @PathVariable Long sellerId) {
        return ResponseEntity.ok(UserRestMapper.toSellerDTO(administratorPort.consultSeller(user, sellerId)));
    }

    @PutMapping("/sellers/{sellerId}")
    public ResponseEntity<SellerResponseDTO> updateSeller(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long sellerId,
                                                          @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toSellerDTO(administratorPort.updateSellerInformation(
                user, sellerId, request.name(), toEmailOrNull(request.email()))));
    }

    @PatchMapping("/sellers/{sellerId}/status")
    public ResponseEntity<SellerResponseDTO> changeSellerStatus(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long sellerId,
                                                                @Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toSellerDTO(administratorPort.changeSellerStatus(
                user, sellerId, enumOf(SellerStatus.class, request.status()))));
    }

    // ---- Warehouses ----
    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponseDTO> registerWarehouse(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterWarehouseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(WarehouseRestMapper.toDTO(
                administratorPort.registerWarehouse(user, enumOf(WarehouseType.class, request.type()),
                        request.sellerId(), CommonRestMapper.toDomain(request.location()))));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> warehouse(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long warehouseId) {
        return ResponseEntity.ok(WarehouseRestMapper.toDTO(administratorPort.consultWarehouse(user, warehouseId)));
    }

    @PutMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long warehouseId,
                                                                @Valid @RequestBody UpdateWarehouseRequestDTO request) {
        return ResponseEntity.ok(WarehouseRestMapper.toDTO(administratorPort.updateWarehouse(
                user, warehouseId, CommonRestMapper.toDomain(request.location()))));
    }

    @PatchMapping("/warehouses/{warehouseId}/status")
    public ResponseEntity<WarehouseResponseDTO> changeWarehouseStatus(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long warehouseId,
            @Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(WarehouseRestMapper.toDTO(administratorPort.changeWarehouseStatus(
                user, warehouseId, enumOf(WarehouseStatus.class, request.status()))));
    }

    // ---- Catalog ----
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> catalog(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ProductRestMapper.toDTOs(administratorPort.consultCatalog(user)));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> product(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(administratorPort.consultProduct(user, productId)));
    }

    @PostMapping("/products/{productId}/publish")
    public ResponseEntity<ProductResponseDTO> publish(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(administratorPort.publishProduct(user, productId)));
    }

    @PostMapping("/products/{productId}/suspend")
    public ResponseEntity<ProductResponseDTO> suspend(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(administratorPort.suspendProduct(user, productId)));
    }

    @PostMapping("/products/{productId}/discontinue")
    public ResponseEntity<ProductResponseDTO> discontinue(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(administratorPort.discontinueProduct(user, productId)));
    }

    // ---- Inventory ----
    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponseDTO> registerInitialInventory(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterInitialInventoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryRestMapper.toDTO(
                administratorPort.registerInitialInventory(user, request.productId(), new Sku(request.sku()),
                        request.warehouseId(), request.initialQuantity())));
    }

    @PostMapping("/inventory/{inventoryId}/entries")
    public ResponseEntity<InventoryResponseDTO> registerEntry(@AuthenticationPrincipal(expression = "user") User user,
                                                              @PathVariable Long inventoryId,
                                                              @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                administratorPort.registerInventoryEntry(user, inventoryId, request.quantity())));
    }

    @PostMapping("/inventory/reservations")
    public ResponseEntity<List<InventoryAllocationResponseDTO>> reserve(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody ReserveInventoryRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toAllocationDTOs(administratorPort.reserveInventory(
                user, request.productId(), new Sku(request.sku()), request.quantity())));
    }

    @PostMapping("/inventory/{inventoryId}/sale-dispatches")
    public ResponseEntity<InventoryResponseDTO> registerSaleDispatch(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                administratorPort.registerSaleDispatch(user, inventoryId, request.quantity())));
    }

    @PostMapping("/inventory/{inventoryId}/adjustments")
    public ResponseEntity<InventoryResponseDTO> adjust(@AuthenticationPrincipal(expression = "user") User user,
                                                       @PathVariable Long inventoryId,
                                                       @Valid @RequestBody AdjustmentRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                administratorPort.adjustInventory(user, inventoryId, request.delta())));
    }

    @PostMapping("/inventory/{inventoryId}/returns")
    public ResponseEntity<InventoryResponseDTO> registerInventoryReturn(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                administratorPort.registerInventoryReturn(user, inventoryId, request.quantity())));
    }

    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<List<InventoryResponseDTO>> inventory(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long productId,
                                                                @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(InventoryRestMapper.toDTOs(administratorPort.consultInventory(user, productId, warehouseId)));
    }

    // ---- Orders, billing and shipments ----
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> order(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(administratorPort.consultOrder(user, orderId)));
    }

    @GetMapping("/buyers/{buyerId}/orders")
    public ResponseEntity<List<OrderResponseDTO>> ordersOfBuyer(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long buyerId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTOs(administratorPort.consultOrdersByBuyer(user, buyerId)));
    }

    @PostMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderResponseDTO> pay(@AuthenticationPrincipal(expression = "user") User user,
                                                @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(administratorPort.payOrder(user, orderId)));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@AuthenticationPrincipal(expression = "user") User user,
                                                              @PathVariable Long orderId,
                                                              @Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(administratorPort.updateOrderStatus(
                user, orderId, enumOf(OrderStatus.class, request.status()))));
    }

    @PostMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceResponseDTO> generateInvoice(@AuthenticationPrincipal(expression = "user") User user,
                                                              @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderRestMapper.toInvoiceDTO(administratorPort.generateInvoice(user, orderId)));
    }

    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceResponseDTO> invoice(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toInvoiceDTO(administratorPort.consultInvoice(user, orderId)));
    }

    @PostMapping("/orders/{orderId}/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> prepareShipments(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderRestMapper.toShipmentDTOs(administratorPort.prepareShipment(user, orderId)));
    }

    @GetMapping("/orders/{orderId}/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> shipmentsOfOrder(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTOs(administratorPort.consultShipmentsByOrder(user, orderId)));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ShipmentResponseDTO> shipment(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(administratorPort.consultShipment(user, shipmentId)));
    }

    @PostMapping("/shipments/{shipmentId}/dispatch")
    public ResponseEntity<ShipmentResponseDTO> dispatch(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(administratorPort.dispatchShipment(user, shipmentId)));
    }

    @PostMapping("/shipments/{shipmentId}/delivery")
    public ResponseEntity<ShipmentResponseDTO> confirmDelivery(@AuthenticationPrincipal(expression = "user") User user,
                                                               @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(administratorPort.confirmDelivery(user, shipmentId)));
    }

    // ---- Returns and refunds ----
    @PostMapping("/returns/{returnId}/receive")
    public ResponseEntity<ReturnResponseDTO> receiveReturn(@AuthenticationPrincipal(expression = "user") User user,
                                                           @PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRestMapper.toReturnDTO(administratorPort.receiveReturn(user, returnId)));
    }

    @PostMapping("/returns/{returnId}/refund")
    public ResponseEntity<RefundResponseDTO> generateRefund(@AuthenticationPrincipal(expression = "user") User user,
                                                            @PathVariable Long returnId,
                                                            @Valid @RequestBody GenerateRefundRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnRestMapper.toRefundDTO(
                administratorPort.generateRefund(user, returnId, CommonRestMapper.toDomain(request.amount()))));
    }

    @PostMapping("/returns/{returnId}/refund/approve")
    public ResponseEntity<RefundResponseDTO> approveRefund(@AuthenticationPrincipal(expression = "user") User user,
                                                           @PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRestMapper.toRefundDTO(administratorPort.approveRefund(user, returnId)));
    }

    @PostMapping("/returns/{returnId}/refund/reject")
    public ResponseEntity<RefundResponseDTO> rejectRefund(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRestMapper.toRefundDTO(administratorPort.rejectRefund(user, returnId)));
    }

    // ---- Reports and audit ----
    @GetMapping("/reports")
    public ResponseEntity<AdministrativeReportResponseDTO> report(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ReportRestMapper.toReportDTO(administratorPort.consultAdministrativeReport(user)));
    }

    @GetMapping("/audit/users/{userId}")
    public ResponseEntity<List<AuditLogResponseDTO>> auditByUser(@AuthenticationPrincipal(expression = "user") User user,
                                                                 @PathVariable Long userId) {
        return ResponseEntity.ok(ReportRestMapper.toAuditDTOs(administratorPort.consultAuditLogByUser(user, userId)));
    }

    @GetMapping("/audit/entities/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponseDTO>> auditByEntity(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable String entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(ReportRestMapper.toAuditDTOs(
                administratorPort.consultAuditLogByEntity(user, entityType, entityId)));
    }
}
