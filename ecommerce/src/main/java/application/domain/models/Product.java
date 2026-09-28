package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Product represents un bien físico o digital que puede ser ofrecido
 * dentro del catalog de NexusMarket.
 *
 * Rules (Domain Model — NexusMarket, sección 7 / 21):
 * - Can ser físico o digital.
 * - Can tener variants.
 * - Has un status dentro del catalog.
 * - The products físicos requieren inventory y despacho.
 * - The products digitales tienen entrega inmediata después del pago.
 */
public class Product {

    private final Long productId;
    private final Long sellerId;
    private String productName;
    private final ProductType productType;
    private final List<ProductVariant> variants = new ArrayList<>();
    private ProductStatus status;
    private Money currentPrice;

    public Product(Long productId, Long sellerId, String productName,
                     ProductType productType, ProductStatus status, Money currentPrice) {
        this.productId = Objects.requireNonNull(productId, "productId es obligatorio");
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId es obligatorio");
        this.productType = Objects.requireNonNull(productType, "productType es obligatorio");
        this.status = Objects.requireNonNull(status, "status es obligatorio");
        this.currentPrice = Objects.requireNonNull(currentPrice, "currentPrice es obligatorio");
        setProductName(productName);
    }

    public void setProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName es obligatorio");
        }
        this.productName = productName;
    }

    public void addVariant(ProductVariant variant) {
        variants.add(Objects.requireNonNull(variant));
    }

    public void changeStatus(ProductStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus);
    }

    public void updatePrice(Money nuevoPrecio) {
        this.currentPrice = Objects.requireNonNull(nuevoPrecio);
    }

    /** true si el product requiere inventory y despacho (regla de negocio). */
    public boolean requiresInventoryAndShipping() {
        return productType == ProductType.PHYSICAL;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Money getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
