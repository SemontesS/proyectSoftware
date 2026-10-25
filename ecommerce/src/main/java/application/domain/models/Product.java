package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @EqualsAndHashCode.Include
    private final Long productId;
    private final Long sellerId;
    private String productName;
    private final ProductType productType;
    @Getter(AccessLevel.NONE)
    private final List<ProductVariant> variants = new ArrayList<>();
    private ProductStatus status;
    private Money currentPrice;

    public Product(Long productId, Long sellerId, String productName, ProductType productType,
                   ProductStatus status, Money currentPrice) {
        this.productId = Objects.requireNonNull(productId, "Product ID is required.");
        this.sellerId = Objects.requireNonNull(sellerId, "Seller ID is required.");
        this.productType = Objects.requireNonNull(productType, "Product type is required.");
        this.status = Objects.requireNonNull(status, "Product status is required.");
        this.currentPrice = Objects.requireNonNull(currentPrice, "Current price is required.");
        changeProductName(productName);
    }

    public void changeProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        this.productName = productName.trim();
    }

    public void addVariant(ProductVariant variant) {
        Objects.requireNonNull(variant, "Product variant is required.");
        boolean duplicateSku = variants.stream().anyMatch(existing -> existing.sku().equals(variant.sku()));
        if (duplicateSku) throw new IllegalArgumentException("Variant SKU must be unique within a product.");
        variants.add(variant);
    }

    public void changeStatus(ProductStatus status) {
        this.status = Objects.requireNonNull(status, "Product status is required.");
    }

    public void updatePrice(Money price) {
        this.currentPrice = Objects.requireNonNull(price, "Product price is required.");
    }

    public boolean requiresInventoryAndShipping() {
        return productType == ProductType.PHYSICAL;
    }

    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }
}
