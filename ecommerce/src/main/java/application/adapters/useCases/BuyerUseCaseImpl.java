package application.adapters.useCases;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.services.audit.RecordAuditService;
import application.domain.services.billing.ConsultInvoiceService;
import application.domain.services.billing.GenerateInvoiceService;
import application.domain.services.buyer.ConsultBuyerService;
import application.domain.services.buyer.UpdateBuyerInformationService;
import application.domain.services.cart.AddProductToCartService;
import application.domain.services.cart.ConsultCartService;
import application.domain.services.cart.RemoveProductFromCartService;
import application.domain.services.cart.UpdateCartItemService;
import application.domain.services.logistics.ConsultShipmentService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.order.CreateOrderService;
import application.domain.services.order.UpdateOrderStatusService;
import application.domain.services.order.ValidateOrderPaymentService;
import application.domain.services.product.ConsultProductService;
import application.domain.services.returns.RequestReturnService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.UpdateUserService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BuyerUseCaseImpl implements BuyerPort {
    private final ConsultUserService consultUserService;
    private final UpdateUserService updateUserService;
    private final ChangeUserPasswordService changeUserPasswordService;
    private final ConsultBuyerService consultBuyerService;
    private final UpdateBuyerInformationService updateBuyerInformationService;
    private final ConsultProductService consultProductService;
    private final AddProductToCartService addProductToCartService;
    private final UpdateCartItemService updateCartItemService;
    private final RemoveProductFromCartService removeProductFromCartService;
    private final ConsultCartService consultCartService;
    private final CreateOrderService createOrderService;
    private final ValidateOrderPaymentService validateOrderPaymentService;
    private final UpdateOrderStatusService updateOrderStatusService;
    private final ConsultOrderService consultOrderService;
    private final GenerateInvoiceService generateInvoiceService;
    private final ConsultInvoiceService consultInvoiceService;
    private final ConsultShipmentService consultShipmentService;
    private final RequestReturnService requestReturnService;
    private final RecordAuditService audit;

    @Override
    public User consultUser(User requestingUser, Long userId) {
        return consultUserService.consult(requestingUser, userId);
    }

    @Override
    public User updateUser(User requestingUser, Long userId, String name, Email email) {
        User user = updateUserService.update(requestingUser, userId, name, email);
        audit.record(requestingUser, Permission.USER_UPDATE, AuditEntityType.USER, user.getUserId(), "User data updated");
        return user;
    }

    @Override
    public void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword) {
        changeUserPasswordService.change(requestingUser, currentPassword, newPassword);
        audit.record(requestingUser, Permission.USER_UPDATE, AuditEntityType.USER, requestingUser.getUserId(), "Password changed");
    }

    @Override
    public Buyer consultBuyer(User requestingUser, Long buyerId) {
        return consultBuyerService.consult(requestingUser, buyerId);
    }

    @Override
    public Buyer updateBuyerInformation(User requestingUser, Long buyerId, Address newPrimaryAddress,
                                        List<Address> addressesToAdd) {
        Buyer buyer = updateBuyerInformationService.update(requestingUser, buyerId, newPrimaryAddress, addressesToAdd);
        audit.record(requestingUser, Permission.BUYER_UPDATE, AuditEntityType.BUYER, buyer.getUserId(), "Addresses updated");
        return buyer;
    }

    @Override
    public Product consultProduct(User requestingUser, Long productId) {
        return consultProductService.consult(requestingUser, productId);
    }

    @Override
    public List<Product> consultCatalog(User requestingUser) {
        return consultProductService.consultCatalog(requestingUser);
    }

    @Override
    public Cart addProductToCart(User requestingUser, Long productId, Sku sku, int quantity) {
        Cart cart = addProductToCartService.add(requestingUser, productId, sku, quantity);
        audit.record(requestingUser, Permission.CART_MANAGE, AuditEntityType.CART, cart.getCartId(),
                "Added product " + productId + " x" + quantity);
        return cart;
    }

    @Override
    public Cart updateCartItem(User requestingUser, Long productId, Sku sku, int quantity) {
        Cart cart = updateCartItemService.update(requestingUser, productId, sku, quantity);
        audit.record(requestingUser, Permission.CART_MANAGE, AuditEntityType.CART, cart.getCartId(),
                "Product " + productId + " quantity set to " + quantity);
        return cart;
    }

    @Override
    public Cart removeProductFromCart(User requestingUser, Long productId) {
        Cart cart = removeProductFromCartService.remove(requestingUser, productId);
        audit.record(requestingUser, Permission.CART_MANAGE, AuditEntityType.CART, cart.getCartId(), "Removed product " + productId);
        return cart;
    }

    @Override
    public Cart consultCart(User requestingUser) {
        return consultCartService.consult(requestingUser);
    }

    @Override
    public Order createOrder(User requestingUser) {
        Order order = createOrderService.create(requestingUser);
        audit.record(requestingUser, Permission.ORDER_CREATE, AuditEntityType.ORDER, order.getOrderId(), "Order created from cart");
        return order;
    }

    @Override
    public Order payOrder(User requestingUser, Long orderId) {
        Order order = validateOrderPaymentService.validate(requestingUser, orderId);
        audit.record(requestingUser, Permission.ORDER_VALIDATE_PAYMENT, AuditEntityType.ORDER, orderId, "Payment validated");
        return order;
    }

    @Override
    public Order cancelOrder(User requestingUser, Long orderId) {
        Order order = updateOrderStatusService.update(requestingUser, orderId, OrderStatus.CANCELLED);
        audit.record(requestingUser, Permission.ORDER_UPDATE_STATUS, AuditEntityType.ORDER, orderId, "Order cancelled");
        return order;
    }

    @Override
    public Order consultOrder(User requestingUser, Long orderId) {
        return consultOrderService.consult(requestingUser, orderId);
    }

    @Override
    public List<Order> consultMyOrders(User requestingUser) {
        return consultOrderService.consultByBuyer(requestingUser, requestingUser.getUserId());
    }

    @Override
    public Invoice generateInvoice(User requestingUser, Long orderId) {
        Invoice invoice = generateInvoiceService.generate(requestingUser, orderId);
        audit.record(requestingUser, Permission.INVOICE_GENERATE, AuditEntityType.INVOICE, invoice.getInvoiceId(),
                "Invoice for order " + orderId);
        return invoice;
    }

    @Override
    public Invoice consultInvoice(User requestingUser, Long orderId) {
        return consultInvoiceService.consultByOrder(requestingUser, orderId);
    }

    @Override
    public Shipment consultShipment(User requestingUser, Long shipmentId) {
        return consultShipmentService.consult(requestingUser, shipmentId);
    }

    @Override
    public List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId) {
        return consultShipmentService.consultByOrder(requestingUser, orderId);
    }

    @Override
    public Return requestReturn(User requestingUser, Long orderId, String reason) {
        Return returnRequest = requestReturnService.request(requestingUser, orderId, reason);
        audit.record(requestingUser, Permission.RETURN_REQUEST, AuditEntityType.RETURN, returnRequest.getReturnId(),
                "Return for order " + orderId);
        return returnRequest;
    }
}
