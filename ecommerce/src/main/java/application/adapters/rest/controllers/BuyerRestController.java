package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.AddCartItemRequestDTO;
import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.RequestReturnRequestDTO;
import application.adapters.rest.dtos.requests.UpdateBuyerAddressesRequestDTO;
import application.adapters.rest.dtos.requests.UpdateCartItemRequestDTO;
import application.adapters.rest.dtos.requests.UpdateUserRequestDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.CartResponseDTO;
import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.ReturnResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.mappers.CartRestMapper;
import application.adapters.rest.mappers.CommonRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerRestController {
    private final BuyerPort buyerPort;

    // ---- Account ----
    @GetMapping("/profile")
    public ResponseEntity<BuyerResponseDTO> profile(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(UserRestMapper.toBuyerDTO(buyerPort.consultBuyer(user, user.getUserId())));
    }

    @PutMapping("/profile/addresses")
    public ResponseEntity<BuyerResponseDTO> updateAddresses(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody UpdateBuyerAddressesRequestDTO request) {
        Address primary = request.primaryAddress() == null ? null : CommonRestMapper.toDomain(request.primaryAddress());
        List<Address> additional = request.additionalAddresses() == null ? null
                : request.additionalAddresses().stream().map(CommonRestMapper::toDomain).toList();
        return ResponseEntity.ok(UserRestMapper.toBuyerDTO(
                buyerPort.updateBuyerInformation(user, user.getUserId(), primary, additional)));
    }

    @GetMapping("/account")
    public ResponseEntity<UserResponseDTO> account(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(UserRestMapper.toDTO(buyerPort.consultUser(user, user.getUserId())));
    }

    @PutMapping("/account")
    public ResponseEntity<UserResponseDTO> updateAccount(@AuthenticationPrincipal(expression = "user") User user,
                                                         @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toDTO(buyerPort.updateUser(user, user.getUserId(),
                request.name(), CommonRestMapper.toEmailOrNull(request.email()))));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        buyerPort.changePassword(user, new RawPassword(request.currentPassword()), new RawPassword(request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    // ---- Catalog ----
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> catalog(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ProductRestMapper.toDTOs(buyerPort.consultCatalog(user)));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> product(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(ProductRestMapper.toDTO(buyerPort.consultProduct(user, productId)));
    }

    // ---- Cart ----
    @GetMapping("/cart")
    public ResponseEntity<CartResponseDTO> cart(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(CartRestMapper.toDTO(buyerPort.consultCart(user)));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponseDTO> addItem(@AuthenticationPrincipal(expression = "user") User user,
                                                   @Valid @RequestBody AddCartItemRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CartRestMapper.toDTO(
                buyerPort.addProductToCart(user, request.productId(), new Sku(request.sku()), request.quantity())));
    }

    @PutMapping("/cart/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateItem(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId,
                                                      @Valid @RequestBody UpdateCartItemRequestDTO request) {
        return ResponseEntity.ok(CartRestMapper.toDTO(
                buyerPort.updateCartItem(user, productId, new Sku(request.sku()), request.quantity())));
    }

    @DeleteMapping("/cart/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeItem(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long productId) {
        return ResponseEntity.ok(CartRestMapper.toDTO(buyerPort.removeProductFromCart(user, productId)));
    }

    // ---- Orders ----
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderRestMapper.toOrderDTO(buyerPort.createOrder(user)));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> myOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTOs(buyerPort.consultMyOrders(user)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> order(@AuthenticationPrincipal(expression = "user") User user,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(buyerPort.consultOrder(user, orderId)));
    }

    @PostMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderResponseDTO> pay(@AuthenticationPrincipal(expression = "user") User user,
                                                @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(buyerPort.payOrder(user, orderId)));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancel(@AuthenticationPrincipal(expression = "user") User user,
                                                   @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toOrderDTO(buyerPort.cancelOrder(user, orderId)));
    }

    @PostMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceResponseDTO> generateInvoice(@AuthenticationPrincipal(expression = "user") User user,
                                                              @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderRestMapper.toInvoiceDTO(buyerPort.generateInvoice(user, orderId)));
    }

    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceResponseDTO> invoice(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toInvoiceDTO(buyerPort.consultInvoice(user, orderId)));
    }

    @GetMapping("/orders/{orderId}/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> shipmentsOfOrder(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTOs(buyerPort.consultShipmentsByOrder(user, orderId)));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ShipmentResponseDTO> shipment(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable Long shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toShipmentDTO(buyerPort.consultShipment(user, shipmentId)));
    }

    // ---- Returns ----
    @PostMapping("/orders/{orderId}/returns")
    public ResponseEntity<ReturnResponseDTO> requestReturn(@AuthenticationPrincipal(expression = "user") User user,
                                                           @PathVariable Long orderId,
                                                           @Valid @RequestBody RequestReturnRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ReturnRestMapper.toReturnDTO(buyerPort.requestReturn(user, orderId, request.reason())));
    }
}
