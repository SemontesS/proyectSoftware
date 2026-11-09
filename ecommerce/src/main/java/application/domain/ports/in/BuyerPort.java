package application.domain.ports.in;

import application.domain.models.*;
import application.domain.valueobjects.*;

import java.util.List;

/** Operations of the BUYER role. */
public interface BuyerPort {
    // Account
    User consultUser(User requestingUser, Long userId);
    User updateUser(User requestingUser, Long userId, String name, Email email);
    void changePassword(User requestingUser, RawPassword currentPassword, RawPassword newPassword);
    Buyer consultBuyer(User requestingUser, Long buyerId);
    Buyer updateBuyerInformation(User requestingUser, Long buyerId, Address newPrimaryAddress, List<Address> addressesToAdd);

    // Catalog
    Product consultProduct(User requestingUser, Long productId);
    List<Product> consultCatalog(User requestingUser);

    // Cart
    Cart addProductToCart(User requestingUser, Long productId, Sku sku, int quantity);
    Cart updateCartItem(User requestingUser, Long productId, Sku sku, int quantity);
    Cart removeProductFromCart(User requestingUser, Long productId);
    Cart consultCart(User requestingUser);

    // Orders
    Order createOrder(User requestingUser);
    Order payOrder(User requestingUser, Long orderId);
    Order cancelOrder(User requestingUser, Long orderId);
    Order consultOrder(User requestingUser, Long orderId);
    List<Order> consultMyOrders(User requestingUser);
    Invoice generateInvoice(User requestingUser, Long orderId);
    Invoice consultInvoice(User requestingUser, Long orderId);
    Shipment consultShipment(User requestingUser, Long shipmentId);
    List<Shipment> consultShipmentsByOrder(User requestingUser, Long orderId);

    // Returns
    Return requestReturn(User requestingUser, Long orderId, String reason);
}
