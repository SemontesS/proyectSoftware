package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Quantity;
import application.domain.valueobjects.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order(1L, 2L, null, Currency.COP);
        order.addLine(new OrderLine(10L, new Sku("LAP-14"), 3L, 5L, Quantity.positive(2), Money.of(1000L, Currency.COP)));
    }

    @Test
    void startsPendingPayment() {
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }

    @Test
    void totalAddsTheLineSubtotals() {
        order.addLine(new OrderLine(11L, new Sku("AUR"), 3L, null, Quantity.positive(1), Money.of(500L, Currency.COP)));
        assertEquals(0, order.getTotal().amount().compareTo(new BigDecimal("2500")));
    }

    @Test
    void followsTheFullLifecycle() {
        order.confirmPayment();
        order.ship();
        order.deliver();
        order.complete();
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void aCompletedOrderCanNeverBeModified() {
        order.confirmPayment();
        order.ship();
        order.deliver();
        order.complete();
        assertFalse(order.canBeModified());
        assertThrows(BusinessRuleViolationException.class, order::cancel);
        assertThrows(BusinessRuleViolationException.class, () -> order.addLine(
                new OrderLine(12L, new Sku("X"), 3L, 5L, Quantity.positive(1), Money.of(1L, Currency.COP))));
    }

    @Test
    void cannotSkipStates() {
        assertThrows(BusinessRuleViolationException.class, order::ship);
        assertThrows(BusinessRuleViolationException.class, order::deliver);
    }

    @Test
    void cannotBeCancelledAfterDispatch() {
        order.confirmPayment();
        order.ship();
        assertThrows(BusinessRuleViolationException.class, order::cancel);
    }

    @Test
    void canBeCancelledBeforeDispatch() {
        order.confirmPayment();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void lineCurrencyMustMatchTheOrderCurrency() {
        assertThrows(IllegalArgumentException.class, () -> order.addLine(
                new OrderLine(12L, new Sku("X"), 3L, 5L, Quantity.positive(1), Money.of(1L, Currency.USD))));
    }

    @Test
    void detectsPhysicalLinesByTheirWarehouse() {
        assertTrue(order.hasPhysicalLines());
    }

    @Test
    void orderLineRejectsAZeroQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderLine(10L, new Sku("A"), 3L, 5L, Quantity.ZERO, Money.of(1L, Currency.COP)));
    }
}
