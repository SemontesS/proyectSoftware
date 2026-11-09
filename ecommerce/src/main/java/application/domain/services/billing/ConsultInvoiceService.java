package application.domain.services.billing;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** The buyer of the order, administrators and supervisors consult its invoice. */
@Service
@RequiredArgsConstructor
public class ConsultInvoiceService {
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Invoice consultByOrder(User requestingUser, Long orderId) {
        permissions.validate(requestingUser, Permission.INVOICE_CONSULT);
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        resourceAccess.validateOwnerOrRole(requestingUser, order.getBuyerId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return invoiceRepositoryPort.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice of order", orderId));
    }
}
