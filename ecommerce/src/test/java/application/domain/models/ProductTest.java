package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, 2L, "Laptop", ProductType.PHYSICAL, ProductStatus.SUSPENDED,
                Money.of(2_000_000L, Currency.COP));
    }

    @Test
    void cannotBePublishedWithoutVariants() {
        assertThrows(BusinessRuleViolationException.class, product::publish);
    }

    @Test
    void variantSkuMustBeUnique() {
        product.addVariant(new ProductVariant(new Sku("S1"), "Color", "Black"));
        assertThrows(IllegalArgumentException.class,
                () -> product.addVariant(new ProductVariant(new Sku("s1"), "Color", "White")));
    }

    @Test
    void publishesAndSuspends() {
        product.addVariant(new ProductVariant(new Sku("S1"), "Color", "Black"));
        product.publish();
        assertTrue(product.isPublished());
        product.suspend();
        assertFalse(product.isPublished());
        assertEquals(ProductStatus.SUSPENDED, product.getStatus());
    }

    @Test
    void aPublishedProductCannotBePublishedAgain() {
        product.addVariant(new ProductVariant(new Sku("S1"), "Color", "Black"));
        product.publish();
        assertThrows(BusinessRuleViolationException.class, product::publish);
    }

    @Test
    void aDiscontinuedProductCannotBeModified() {
        product.discontinue();
        assertThrows(BusinessRuleViolationException.class, () -> product.changeProductName("Other"));
        assertThrows(BusinessRuleViolationException.class, product::discontinue);
    }

    @Test
    void onlyPhysicalProductsRequireInventoryAndShipping() {
        assertTrue(product.requiresInventoryAndShipping());
        Product digital = new Product(2L, 2L, "Course", ProductType.DIGITAL, ProductStatus.SUSPENDED,
                Money.of(100L, Currency.COP));
        assertFalse(digital.requiresInventoryAndShipping());
    }
}
