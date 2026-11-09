package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.Sku;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Catalog product. Lifecycle: SUSPENDED (registered, not visible) -> PUBLISHED <-> SUSPENDED,
 * and DISCONTINUED (terminal). A product needs at least one variant (SKU) to be published.
 */
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
        this.productName = requireName(productName);
    }

    public void changeProductName(String productName) {
        requireNotDiscontinued();
        this.productName = requireName(productName);
    }

    public void updatePrice(Money price) {
        requireNotDiscontinued();
        this.currentPrice = Objects.requireNonNull(price, "Product price is required.");
    }

    public void addVariant(ProductVariant variant) {
        requireNotDiscontinued();
        Objects.requireNonNull(variant, "Product variant is required.");
        if (hasVariant(variant.sku())) throw new IllegalArgumentException("Variant SKU must be unique within a product.");
        variants.add(variant);
    }

    public boolean hasVariant(Sku sku) {
        return variants.stream().anyMatch(existing -> existing.sku().equals(sku));
    }

    public void publish() {
        if (status != ProductStatus.SUSPENDED) {
            throw new BusinessRuleViolationException("Only a suspended product can be published (current: " + status + ").");
        }
        if (variants.isEmpty()) throw new BusinessRuleViolationException("A product without variants cannot be published.");
        status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        if (status != ProductStatus.PUBLISHED) {
            throw new BusinessRuleViolationException("Only a published product can be suspended (current: " + status + ").");
        }
        status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        requireNotDiscontinued();
        status = ProductStatus.DISCONTINUED;
    }

    public boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }

    public boolean requiresInventoryAndShipping() {
        return productType == ProductType.PHYSICAL;
    }

    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }

    private void requireNotDiscontinued() {
        if (status == ProductStatus.DISCONTINUED) {
            throw new BusinessRuleViolationException("A discontinued product cannot be modified.");
        }
    }

    private static String requireName(String productName) {
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("Product name is required.");
        return productName.trim();
    }
}
