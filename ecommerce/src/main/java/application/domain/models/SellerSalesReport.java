package application.domain.models;

import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;

import java.util.List;
import java.util.Map;

/**
 * Read model of one seller: what was sold, what stock is left, and what purchases, returns and
 * refunds changed. All figures come from persisted data, so the same report is obtained after a restart.
 */
public record SellerSalesReport(
        Long sellerId,
        List<ProductLine> products,
        Map<Currency, Money> grossSales,
        Map<Currency, Money> refundedTotal,
        Map<Currency, Money> netSales) {

    /** Figures of one product variant (SKU). {@code availableStock} is 0 for digital products. */
    public record ProductLine(
            Long productId,
            String productName,
            String productType,
            String sku,
            int unitsSold,
            Money grossSales,
            int unitsReturned,
            int returnsInProgress,
            Money refundedAmount,
            Money netSales,
            int availableStock,
            int reservedStock) {
    }
}
