package application.domain.ports.out;

import application.domain.models.Invoice;
import application.domain.valueobjects.InvoiceNumber;

import java.util.Optional;

public interface InvoiceRepositoryPort {
    Long nextId();

    InvoiceNumber nextInvoiceNumber();

    Invoice save(Invoice invoice);

    Optional<Invoice> findByOrderId(Long orderId);
}
