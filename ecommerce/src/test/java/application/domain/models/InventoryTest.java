package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.Quantity;
import application.domain.valueobjects.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(1L, 10L, new Sku("LAP-14"), 5L, new Quantity(10));
    }

    @Test
    void inboundIncreasesAvailableStockAndRecordsAMovement() {
        inventory.recordInbound(Quantity.positive(5));
        assertEquals(new Quantity(15), inventory.getAvailableQuantity());
        assertEquals(1, inventory.getMovements().size());
        assertEquals(InventoryMovementType.INBOUND, inventory.getMovements().get(0).getType());
    }

    @Test
    void reservationMovesStockFromAvailableToReserved() {
        inventory.recordReservation(Quantity.positive(4));
        assertEquals(new Quantity(6), inventory.getAvailableQuantity());
        assertEquals(new Quantity(4), inventory.getReservedQuantity());
    }

    @Test
    void cannotReserveMoreThanAvailable() {
        assertThrows(BusinessRuleViolationException.class, () -> inventory.recordReservation(Quantity.positive(11)));
        assertEquals(new Quantity(10), inventory.getAvailableQuantity());
    }

    @Test
    void releasingAReservationReturnsStockToAvailable() {
        inventory.recordReservation(Quantity.positive(4));
        inventory.releaseReservation(Quantity.positive(3));
        assertEquals(new Quantity(9), inventory.getAvailableQuantity());
        assertEquals(new Quantity(1), inventory.getReservedQuantity());
    }

    @Test
    void cannotReleaseMoreThanReserved() {
        assertThrows(BusinessRuleViolationException.class, () -> inventory.releaseReservation(Quantity.positive(1)));
    }

    @Test
    void saleOutboundConsumesTheReservedStockFirst() {
        inventory.recordReservation(Quantity.positive(4));
        inventory.recordSaleOutbound(Quantity.positive(3));
        assertEquals(new Quantity(1), inventory.getReservedQuantity());
        assertEquals(new Quantity(6), inventory.getAvailableQuantity());
    }

    @Test
    void markingDamagedMovesStockOutOfAvailable() {
        inventory.markDamaged(Quantity.positive(2));
        assertEquals(new Quantity(8), inventory.getAvailableQuantity());
        assertEquals(new Quantity(2), inventory.getDamagedQuantity());
    }

    @Test
    void adjustmentAcceptsSignedDeltas() {
        inventory.recordAdjustment(-3);
        assertEquals(new Quantity(7), inventory.getAvailableQuantity());
        inventory.recordAdjustment(2);
        assertEquals(new Quantity(9), inventory.getAvailableQuantity());
    }

    @Test
    void stockCanNeverBecomeNegative() {
        assertThrows(BusinessRuleViolationException.class, () -> inventory.recordAdjustment(-11));
        assertThrows(IllegalArgumentException.class, () -> inventory.recordAdjustment(0));
        assertEquals(new Quantity(10), inventory.getAvailableQuantity());
    }

    @Test
    void operationsRequireAPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> inventory.recordInbound(Quantity.ZERO));
    }
}
