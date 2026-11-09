package application.domain.services.returns;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RefundStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

/**
 * Generates the PENDING refund of a received return. The amount must use the order currency and
 * the refunds of the order (excluding rejected ones) cannot exceed the order total.
 */
@Service
@RequiredArgsConstructor
public class GenerateRefundService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final Clock clock;

    public Refund generate(User requestingUser, Long returnId, Money amount) {
        permissions.validate(requestingUser, Permission.REFUND_GENERATE);
        Return returnRequest = returnRepositoryPort.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
        Order order = orderRepositoryPort.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", returnRequest.getOrderId()));

        if (amount.currency() != order.getCurrency()) {
            throw new BusinessRuleViolationException("The refund currency must match the order currency.");
        }
        BigDecimal alreadyRefunded = returnRepositoryPort.findByOrderId(order.getOrderId()).stream()
                .flatMap(other -> other.getRefund().stream())
                .filter(refund -> refund.getStatus() != RefundStatus.REJECTED)
                .map(refund -> refund.getAmount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (alreadyRefunded.add(amount.amount()).compareTo(order.getTotal().amount()) > 0) {
            throw new BusinessRuleViolationException("The refunds of the order cannot exceed the order total.");
        }

        Refund refund = returnRequest.generateRefund(returnRepositoryPort.nextRefundId(), amount, LocalDate.now(clock));
        returnRepositoryPort.save(returnRequest);
        return refund;
    }
}
