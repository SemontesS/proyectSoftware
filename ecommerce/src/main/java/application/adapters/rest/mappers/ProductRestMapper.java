package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.domain.models.Product;

import java.util.List;

public final class ProductRestMapper {

    private ProductRestMapper() {
    }

    public static ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(product.getProductId(), product.getSellerId(), product.getProductName(),
                product.getProductType().name(), product.getStatus().name(),
                CommonRestMapper.toDTO(product.getCurrentPrice()),
                product.getVariants().stream().map(CommonRestMapper::toDTO).toList());
    }

    public static List<ProductResponseDTO> toDTOs(List<Product> products) {
        return products.stream().map(ProductRestMapper::toDTO).toList();
    }
}
