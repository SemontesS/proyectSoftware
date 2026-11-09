package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static Product toDomain(ProductJpaEntity entity) {
        List<ProductVariant> variants = entity.getVariants().stream()
                .map(v -> new ProductVariant(new Sku(v.getSku()), v.getAttribute(), v.getValue()))
                .toList();
        return Product.restore(entity.getId(), entity.getSellerId(), entity.getName(),
                ProductType.valueOf(entity.getType()), ProductStatus.valueOf(entity.getStatus()),
                JpaCommonMapper.toMoney(entity.getPriceAmount(), entity.getPriceCurrency()), variants);
    }

    public static void updateEntity(ProductJpaEntity entity, Product product) {
        entity.setId(product.getProductId());
        entity.setSellerId(product.getSellerId());
        entity.setName(product.getProductName());
        entity.setType(product.getProductType().name());
        entity.setStatus(product.getStatus().name());
        entity.setPriceAmount(product.getCurrentPrice().amount());
        entity.setPriceCurrency(product.getCurrentPrice().currency().name());
        entity.getVariants().clear();
        product.getVariants().forEach(v ->
                entity.getVariants().add(new VariantEmbeddable(v.sku().value(), v.attribute(), v.value())));
    }
}
