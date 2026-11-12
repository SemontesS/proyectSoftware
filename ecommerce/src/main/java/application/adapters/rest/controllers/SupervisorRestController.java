package application.adapters.rest.controllers;

import application.domain.valueobjects.AuditEntityType;
import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.UpdateUserRequestDTO;
import application.adapters.rest.dtos.responses.AdministrativeReportResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.InventoryResponseDTO;
import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.CommonRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.ReportRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.SupervisorPort;
import application.domain.valueobjects.RawPassword;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supervisor")
@RequiredArgsConstructor
public class SupervisorRestController {
    private final SupervisorPort supervisorPort;

    // ---- Account ----
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> user(@AuthenticationPrincipal(expression = "user") User user,
                                                @PathVariable Long userId) {
        return ResponseEntity.ok(UserRestMapper.toDTO(supervisorPort.consultUser(user, userId)));
    }

    @PutMapping("/account")
    public ResponseEntity<UserResponseDTO> updateAccount(@AuthenticationPrincipal(expression = "user") User user,
                                                         @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toDTO(supervisorPort.updateUser(user, user.getUserId(),
                request.name(), CommonRestMapper.toEmailOrNull(request.email()))));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        supervisorPort.changePassword(user, new RawPassword(request.currentPassword()), new RawPassword(request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    // ---- Consultations ----
    @GetMapping("/buyers/{buyerId}")
    public ResponseEntity<BuyerResponseDTO> buyer(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long buyerId) {
        return ResponseEntity.ok(UserRestMapper.toBuyerDTO(supervisorPort.consultBuyer(user, buyerId)));
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<SellerResponseDTO> seller(@AuthenticationPrincipal(expression = "user") User user,
                                                    @PathVariable Long sellerId) {
        return ResponseEntity.ok(UserRestMapper.toSellerDTO(supervisorPort.consultSeller(user, sellerId)));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> warehouse(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long warehouseId) {
        return ResponseEntity.ok(WarehouseRestMapper.toDTO(supervisorPort.consultWarehouse(user, warehouseId)));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> catalog(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ProductRestMapper.toDTOs(supervisorPort.consultCatalog(user)));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> product(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(supervisorPort.consultProduct(user, productId)));
    }

    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<List<InventoryResponseDTO>> inventory(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long productId,
                                                                @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(InventoryRestMapper.toDTOs(supervisorPort.consultInventory(user, productId, warehouseId)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> order(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(supervisorPort.consultOrder(user, orderId)));
    }

    @GetMapping("/buyers/{buyerId}/orders")
    public ResponseEntity<List<OrderResponseDTO>> ordersOfBuyer(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long buyerId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTOs(supervisorPort.consultOrdersByBuyer(user, buyerId)));
    }

    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceResponseDTO> invoice(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toInvoiceDTO(supervisorPort.consultInvoice(user, orderId)));
    }

    @GetMapping("/orders/{orderId}/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> shipmentsOfOrder(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTOs(supervisorPort.consultShipmentsByOrder(user, orderId)));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ShipmentResponseDTO> shipment(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(supervisorPort.consultShipment(user, shipmentId)));
    }

    // ---- Reports and audit ----
    @GetMapping("/reports")
    public ResponseEntity<AdministrativeReportResponseDTO> report(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ReportRestMapper.toReportDTO(supervisorPort.consultAdministrativeReport(user)));
    }

    @GetMapping("/audit/users/{userId}")
    public ResponseEntity<List<AuditLogResponseDTO>> auditByUser(@AuthenticationPrincipal(expression = "user") User user,
                                                                 @PathVariable Long userId) {
        return ResponseEntity.ok(ReportRestMapper.toAuditDTOs(supervisorPort.consultAuditLogByUser(user, userId)));
    }

    @GetMapping("/audit/entities/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponseDTO>> auditByEntity(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable String entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(ReportRestMapper.toAuditDTOs(
                supervisorPort.consultAuditLogByEntity(user,
                        AuditEntityType.valueOf(entityType.trim().toUpperCase()), entityId)));
    }
}
