package application.domain.services.order;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.InventoryAllocation;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.services.inventory.ReserveInventoryService;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.SellerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirms the buyer's active cart: validates buyer, products and sellers, reserves inventory for
 * physical products (possibly from several warehouses) and creates the order in PENDING_PAYMENT.
 *
 * <p>Everything is validated and planned first; aggregates are only modified once every check
 * passed. The calling use case must still run in one transaction because several aggregates
 * (cart, inventories, order) are persisted.
 */
@Service
@RequiredArgsConstructor
public class CreateOrderService {
    private final OrderRepositoryPort orderRepositoryPort;
    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ReserveInventoryService reserveInventoryService;
    private final ValidateUserPermissionsService permissions;

    public Order create(User requestingUser) {
        permissions.validate(requestingUser, Permission.ORDER_CREATE);
        Buyer buyer = userRepositoryPort.findBuyerById(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer", requestingUser.getUserId()));
        if (buyer.getCommercialStatus() != BuyerStatus.ENABLED) {
            throw new BusinessRuleViolationException("The buyer is restricted and cannot purchase.");
        }
        Cart cart = cartRepositoryPort.findActiveByBuyerId(buyer.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Active cart", buyer.getUserId()));
        cart.validateConfirmable();

        // 1. Validate and plan (no side effects)
        Order order = new Order(orderRepositoryPort.nextId(), buyer.getUserId(), null, cart.getCurrency());
        List<InventoryAllocation> allAllocations = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = productRepositoryPort.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product", item.getProductId()));
            if (!product.isPublished()) {
                throw new BusinessRuleViolationException("Product " + product.getProductId() + " is not available.");
            }
            Seller seller = userRepositoryPort.findSellerById(product.getSellerId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller", product.getSellerId()));
            if (seller.getSellerStatus() != SellerStatus.ACTIVE) {
                throw new BusinessRuleViolationException("The seller of product " + product.getProductId() + " is suspended.");
            }

            if (product.requiresInventoryAndShipping()) {
                List<InventoryAllocation> allocations = reserveInventoryService.plan(
                        item.getProductId(), item.getSku(), item.getQuantity());
                for (InventoryAllocation allocation : allocations) {
                    order.addLine(new OrderLine(item.getProductId(), item.getSku(), product.getSellerId(),
                            allocation.warehouseId(), allocation.quantity(), item.getUnitPrice()));
                }
                allAllocations.addAll(allocations);
            } else {
                order.addLine(new OrderLine(item.getProductId(), item.getSku(), product.getSellerId(),
                        null, item.getQuantity(), item.getUnitPrice()));
            }
        }

        // 2. Apply
        reserveInventoryService.apply(allAllocations);
        cart.confirm();
        cartRepositoryPort.save(cart);
        return orderRepositoryPort.save(order);
    }
}
