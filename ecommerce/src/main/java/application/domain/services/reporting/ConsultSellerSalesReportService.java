package application.domain.services.reporting;

import application.domain.models.Inventory;
import application.domain.models.OrderLine;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.SellerSalesReport;
import application.domain.models.SellerSalesReport.ProductLine;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sales report of one seller. A seller can only read its own; administrators and supervisors can
 * read any. Sales count orders already paid (not pending payment, not cancelled); refunds count
 * only when approved; units returned are those whose return was already received.
 */
@Service
@RequiredArgsConstructor
public class ConsultSellerSalesReportService {
    private static final Set<OrderStatus> NOT_SOLD = Set.of(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED);

    private final ProductRepositoryPort productRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ReturnRepositoryPort returnRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public SellerSalesReport consult(User requestingUser, Long sellerId) {
        permissions.validate(requestingUser, Permission.SELLER_REPORT_CONSULT);
        resourceAccess.validateOwnerOrRole(requestingUser, sellerId, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);

        List<OrderLine> soldLines = orderRepositoryPort.findAll().stream()
                .filter(order -> !NOT_SOLD.contains(order.getStatus()))
                .flatMap(order -> order.getLines().stream())
                .filter(line -> sellerId.equals(line.getSellerId()))
                .toList();
        List<Return> returns = returnRepositoryPort.findAll().stream()
                .filter(returnRequest -> sellerId.equals(returnRequest.getSellerId()))
                .toList();

        List<ProductLine> rows = new ArrayList<>();
        Map<Currency, Money> gross = new EnumMap<>(Currency.class);
        Map<Currency, Money> refunded = new EnumMap<>(Currency.class);
        Map<Currency, Money> net = new EnumMap<>(Currency.class);

        List<Product> products = productRepositoryPort.findAll().stream()
                .filter(product -> sellerId.equals(product.getSellerId()))
                .sorted(Comparator.comparing(Product::getProductId))
                .toList();
        for (Product product : products) {
            Currency currency = product.getCurrentPrice().currency();
            for (ProductVariant variant : product.getVariants()) {
                int unitsSold = 0;
                Money grossSales = Money.zero(currency);
                for (OrderLine line : soldLines) {
                    if (line.getProductId().equals(product.getProductId()) && line.getSku().equals(variant.sku())) {
                        unitsSold += line.getQuantity().value();
                        grossSales = grossSales.add(line.getSubtotal());
                    }
                }
                int unitsReturned = 0;
                int inProgress = 0;
                Money refundedAmount = Money.zero(currency);
                for (Return returnRequest : returns) {
                    if (!returnRequest.getProductId().equals(product.getProductId())
                            || !returnRequest.getSku().equals(variant.sku())) {
                        continue;
                    }
                    if (returnRequest.getStatus() == ReturnStatus.REQUESTED) {
                        inProgress++;
                    } else {
                        unitsReturned += returnRequest.getQuantity().value();
                    }
                    if (returnRequest.getRefund().isPresent()
                            && returnRequest.getRefund().get().getStatus() == RefundStatus.APPROVED) {
                        refundedAmount = refundedAmount.add(returnRequest.getRefund().get().getAmount());
                    }
                }
                int available = 0;
                int reserved = 0;
                for (Inventory inventory : inventoryRepositoryPort.findByProductAndSku(product.getProductId(), variant.sku())) {
                    available += inventory.getAvailableQuantity().value();
                    reserved += inventory.getReservedQuantity().value();
                }
                Money netSales = Money.of(grossSales.amount().subtract(refundedAmount.amount()).max(BigDecimal.ZERO), currency);
                rows.add(new ProductLine(product.getProductId(), product.getProductName(),
                        product.getProductType().name(), variant.sku().value(), unitsSold, grossSales,
                        unitsReturned, inProgress, refundedAmount, netSales, available, reserved));
                gross.merge(currency, grossSales, Money::add);
                refunded.merge(currency, refundedAmount, Money::add);
                net.merge(currency, netSales, Money::add);
            }
        }
        return new SellerSalesReport(sellerId, rows, gross, refunded, net);
    }
}
