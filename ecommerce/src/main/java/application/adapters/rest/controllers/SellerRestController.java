package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.AdjustmentRequestDTO;
import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.QuantityRequestDTO;
import application.adapters.rest.dtos.requests.RegisterInitialInventoryRequestDTO;
import application.adapters.rest.dtos.requests.RegisterProductRequestDTO;
import application.adapters.rest.dtos.requests.UpdateProductRequestDTO;
import application.adapters.rest.dtos.requests.UpdateUserRequestDTO;
import application.adapters.rest.dtos.responses.InventoryResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.CommonRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.SellerPort;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
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
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerRestController {
    private final SellerPort sellerPort;

    // ---- Account ----
    @GetMapping("/profile")
    public ResponseEntity<SellerResponseDTO> profile(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(UserRestMapper.toSellerDTO(sellerPort.consultSeller(user, user.getUserId())));
    }

    @GetMapping("/account")
    public ResponseEntity<UserResponseDTO> account(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(UserRestMapper.toDTO(sellerPort.consultUser(user, user.getUserId())));
    }

    @PutMapping("/account")
    public ResponseEntity<UserResponseDTO> updateAccount(@AuthenticationPrincipal(expression = "user") User user,
                                                         @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toDTO(sellerPort.updateUser(user, user.getUserId(),
                request.name(), CommonRestMapper.toEmailOrNull(request.email()))));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        sellerPort.changePassword(user, new RawPassword(request.currentPassword()), new RawPassword(request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> warehouse(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long warehouseId) {
        return ResponseEntity.ok(WarehouseRestMapper.toDTO(sellerPort.consultWarehouse(user, warehouseId)));
    }

    // ---- Catalog ----
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> registerProduct(@AuthenticationPrincipal(expression = "user") User user,
                                                              @Valid @RequestBody RegisterProductRequestDTO request) {
        List<ProductVariant> variants = request.variants().stream().map(CommonRestMapper::toDomain).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductRestMapper.toDTO(sellerPort.registerProduct(
                user, request.name(), CommonRestMapper.enumOf(ProductType.class, request.type()),
                CommonRestMapper.toDomain(request.price()), variants)));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@AuthenticationPrincipal(expression = "user") User user,
                                                            @PathVariable Long productId,
                                                            @Valid @RequestBody UpdateProductRequestDTO request) {
        Money price = request.price() == null ? null : CommonRestMapper.toDomain(request.price());
        return ResponseEntity.ok(ProductRestMapper.toDTO(
                sellerPort.updateProduct(user, productId, request.name(), price)));
    }

    @PostMapping("/products/{productId}/publish")
    public ResponseEntity<ProductResponseDTO> publish(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(sellerPort.publishProduct(user, productId)));
    }

    @PostMapping("/products/{productId}/suspend")
    public ResponseEntity<ProductResponseDTO> suspend(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(sellerPort.suspendProduct(user, productId)));
    }

    @PostMapping("/products/{productId}/discontinue")
    public ResponseEntity<ProductResponseDTO> discontinue(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(sellerPort.discontinueProduct(user, productId)));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> catalog(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ProductRestMapper.toDTOs(sellerPort.consultCatalog(user)));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> product(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(sellerPort.consultProduct(user, productId)));
    }

    // ---- Inventory ----
    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponseDTO> registerInitialInventory(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterInitialInventoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryRestMapper.toDTO(
                sellerPort.registerInitialInventory(user, request.productId(), new Sku(request.sku()),
                        request.warehouseId(), request.initialQuantity())));
    }

    @PostMapping("/inventory/{inventoryId}/entries")
    public ResponseEntity<InventoryResponseDTO> registerEntry(@AuthenticationPrincipal(expression = "user") User user,
                                                              @PathVariable Long inventoryId,
                                                              @Valid @RequestBody QuantityRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(
                sellerPort.registerInventoryEntry(user, inventoryId, request.quantity())));
    }

    @PostMapping("/inventory/{inventoryId}/adjustments")
    public ResponseEntity<InventoryResponseDTO> adjust(@AuthenticationPrincipal(expression = "user") User user,
                                                       @PathVariable Long inventoryId,
                                                       @Valid @RequestBody AdjustmentRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toDTO(sellerPort.adjustInventory(user, inventoryId, request.delta())));
    }

    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<List<InventoryResponseDTO>> inventory(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable Long productId,
                                                                @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(InventoryRestMapper.toDTOs(sellerPort.consultInventory(user, productId, warehouseId)));
    }

    // ---- Orders ----
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> order(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(sellerPort.consultOrder(user, orderId)));
    }
}
