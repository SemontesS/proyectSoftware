package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReturnTest {

    private Return returnRequest;

    @BeforeEach
    void setUp() {
        returnRequest = new Return(1L, 2L, "Damaged on arrival", LocalDate.now(), ReturnStatus.REQUESTED);
    }

    private Money amount() {
        return Money.of(1000L, Currency.COP);
    }

    @Test
    void aReasonIsRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> new Return(1L, 2L, " ", LocalDate.now(), ReturnStatus.REQUESTED));
    }

    @Test
    void aRefundRequiresAReceivedReturn() {
        assertThrows(BusinessRuleViolationException.class,
                () -> returnRequest.generateRefund(1L, amount(), LocalDate.now()));
    }

    @Test
    void generatesAPendingRefundOnceReceived() {
        returnRequest.receive();
        Refund refund = returnRequest.generateRefund(1L, amount(), LocalDate.now());
        assertEquals(RefundStatus.PENDING, refund.getStatus());
        assertTrue(returnRequest.getRefund().isPresent());
    }

    @Test
    void aReturnGeneratesAtMostOneRefund() {
        returnRequest.receive();
        returnRequest.generateRefund(1L, amount(), LocalDate.now());
        assertThrows(BusinessRuleViolationException.class,
                () -> returnRequest.generateRefund(2L, amount(), LocalDate.now()));
    }

    @Test
    void approvingTheRefundApprovesTheReturn() {
        returnRequest.receive();
        returnRequest.generateRefund(1L, amount(), LocalDate.now());
        Refund refund = returnRequest.approveRefund();
        assertEquals(RefundStatus.APPROVED, refund.getStatus());
        assertEquals(ReturnStatus.APPROVED, returnRequest.getStatus());
    }

    @Test
    void rejectingTheRefundRejectsTheReturn() {
        returnRequest.receive();
        returnRequest.generateRefund(1L, amount(), LocalDate.now());
        returnRequest.rejectRefund();
        assertEquals(ReturnStatus.REJECTED, returnRequest.getStatus());
    }

    @Test
    void cannotDecideARefundThatDoesNotExist() {
        assertThrows(BusinessRuleViolationException.class, returnRequest::approveRefund);
    }

    @Test
    void cannotReceiveTwice() {
        returnRequest.receive();
        assertThrows(BusinessRuleViolationException.class, returnRequest::receive);
    }
}
