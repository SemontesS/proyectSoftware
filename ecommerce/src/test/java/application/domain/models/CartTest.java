package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.CartStatus;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.Quantity;
import application.domain.valueobjects.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart(1L, 2L, null, Currency.COP);
    }

    private static CartItem item(int quantity) {
        return new CartItem(10L, new Sku("LAP-14"), Quantity.positive(quantity), Money.of(1000L, Currency.COP));
    }

    @Test
    void addingTheSameVariantAddsTheQuantities() {
        cart.addItem(item(2));
        cart.addItem(item(2));
        assertEquals(1, cart.getItems().size());
        assertEquals(new Quantity(4), cart.getItems().get(0).getQuantity());
        assertEquals(0, cart.getTotal().amount().compareTo(new BigDecimal("4000")));
    }

    @Test
    void itemCurrencyMustMatchTheCartCurrency() {
        CartItem usd = new CartItem(10L, new Sku("LAP-14"), Quantity.positive(1), Money.of(1L, Currency.USD));
        assertThrows(BusinessRuleViolationException.class, () -> cart.addItem(usd));
    }

    @Test
    void updatingAnItemChangesItsQuantity() {
        cart.addItem(item(2));
        cart.updateItemQuantity(10L, new Sku("LAP-14"), Quantity.positive(7));
        assertEquals(new Quantity(7), cart.getItems().get(0).getQuantity());
    }

    @Test
    void updatingAMissingItemFails() {
        assertThrows(BusinessRuleViolationException.class,
                () -> cart.updateItemQuantity(99L, new Sku("X"), Quantity.positive(1)));
    }

    @Test
    void removingAProductThatIsNotInTheCartFails() {
        assertThrows(BusinessRuleViolationException.class, () -> cart.removeItem(10L));
    }

    @Test
    void anEmptyCartCannotBeConfirmed() {
        assertThrows(BusinessRuleViolationException.class, cart::confirm);
    }

    @Test
    void aConfirmedCartCannotBeModified() {
        cart.addItem(item(1));
        cart.confirm();
        assertEquals(CartStatus.CONFIRMED, cart.getStatus());
        assertThrows(BusinessRuleViolationException.class, () -> cart.addItem(item(1)));
    }

    @Test
    void cartItemRejectsAZeroQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new CartItem(10L, new Sku("A"), Quantity.ZERO, Money.of(1L, Currency.COP)));
    }
}
