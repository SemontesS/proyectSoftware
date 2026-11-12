package application.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueObjectsTest {

    @Test
    void emailIsNormalizedToLowerCase() {
        assertEquals("ana@mail.com", new Email("  Ana@Mail.com ").value());
    }

    @Test
    void emailRejectsInvalidFormats() {
        assertThrows(IllegalArgumentException.class, () -> new Email("not-an-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email(" "));
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void skuIsNormalizedToUpperCase() {
        assertEquals("LAP-14", new Sku(" lap-14 ").value());
    }

    @Test
    void skuIsRequired() {
        assertThrows(IllegalArgumentException.class, () -> new Sku(""));
    }

    @Test
    void rawPasswordEnforcesTheComplexityRules() {
        assertThrows(IllegalArgumentException.class, () -> new RawPassword("short1"));
        assertThrows(IllegalArgumentException.class, () -> new RawPassword("onlyletters"));
        assertThrows(IllegalArgumentException.class, () -> new RawPassword("12345678"));
        assertEquals("Secret123", new RawPassword("Secret123").value());
    }

    @Test
    void rawPasswordIsNeverPrinted() {
        assertFalse(new RawPassword("Secret123").toString().contains("Secret123"));
    }

    @Test
    void moneyRejectsNegativeAmounts() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-1L, Currency.COP));
    }

    @Test
    void moneyCannotMixCurrencies() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of(10L, Currency.COP).add(Money.of(10L, Currency.USD)));
    }

    @Test
    void moneyMultipliesByAQuantity() {
        Money total = Money.of(1500L, Currency.COP).multiply(3);
        assertEquals(0, total.amount().compareTo(new BigDecimal("4500")));
        assertEquals(Currency.COP, total.currency());
    }

    @Test
    void productVariantRequiresAttributeAndValue() {
        assertThrows(IllegalArgumentException.class, () -> new ProductVariant(new Sku("A"), " ", "Black"));
        assertThrows(IllegalArgumentException.class, () -> new ProductVariant(new Sku("A"), "Color", null));
    }
}
