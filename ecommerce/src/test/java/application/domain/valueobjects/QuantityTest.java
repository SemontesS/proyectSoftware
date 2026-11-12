package application.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantityTest {

    @Test
    void rejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(-1));
    }

    @Test
    void positiveRejectsZero() {
        assertThrows(IllegalArgumentException.class, () -> Quantity.positive(0));
    }

    @Test
    void zeroIsAllowedByTheConstructor() {
        assertTrue(new Quantity(0).isZero());
        assertTrue(Quantity.ZERO.isZero());
    }

    @Test
    void plusAndMinusReturnNewValues() {
        Quantity five = Quantity.positive(5);
        assertEquals(new Quantity(8), five.plus(Quantity.positive(3)));
        assertEquals(new Quantity(2), five.minus(Quantity.positive(3)));
    }

    @Test
    void minusCannotGoBelowZero() {
        assertThrows(IllegalArgumentException.class, () -> Quantity.positive(2).minus(Quantity.positive(3)));
    }

    @Test
    void quantitiesAreComparable() {
        assertTrue(Quantity.positive(2).compareTo(Quantity.positive(3)) < 0);
        assertEquals(0, Quantity.positive(3).compareTo(Quantity.positive(3)));
    }
}
