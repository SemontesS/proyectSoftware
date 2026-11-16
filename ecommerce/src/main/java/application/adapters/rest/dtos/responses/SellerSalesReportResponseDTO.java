package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.MoneyDTO;

import java.util.List;
import java.util.Map;

public record SellerSalesReportResponseDTO(Long sellerId, List<ProductLineDTO> products,
        Map<String, MoneyDTO> grossSales, Map<String, MoneyDTO> refundedTotal, Map<String, MoneyDTO> netSales) {

    public record ProductLineDTO(Long productId, String productName, String productType, String sku,
            int unitsSold, MoneyDTO grossSales, int unitsReturned, int returnsInProgress,
            MoneyDTO refundedAmount, MoneyDTO netSales, int availableStock, int reservedStock) {}
}
