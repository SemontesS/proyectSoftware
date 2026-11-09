package application.domain.services.billing;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Generates the invoice of a paid order (one per order). Taxes are not defined by the
 * specification yet, so subtotal and total are both the order total.
 */
@Service
@RequiredArgsConstructor
public class GenerateInvoiceService {
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;
    private final Clock clock;

    public Invoice generate(User requestingUser, Long orderId) {
        permissions.validate(requestingUser, Permission.INVOICE_GENERATE);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId(), UserRole.ADMINISTRATOR);

        if (order.getStatus() == OrderStatus.PENDING_PAYMENT || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleViolationException("An invoice requires a paid order (status: " + order.getStatus() + ").");
        }
        if (invoiceRepositoryPort.findByOrderId(orderId).isPresent()) {
            throw new DuplicateEntityException("The order already has an invoice.");
        }
        Money total = order.getTotal();
        Invoice invoice = new Invoice(invoiceRepositoryPort.nextId(), orderId,
                invoiceRepositoryPort.nextInvoiceNumber(), LocalDate.now(clock), total, total);
        return invoiceRepositoryPort.save(invoice);
    }
}
