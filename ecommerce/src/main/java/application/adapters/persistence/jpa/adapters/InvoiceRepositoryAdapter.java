package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.mappers.*;
import application.adapters.persistence.jpa.repositories.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepositoryPort {
    private final InvoiceJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("invoices");
    }

    @Override
    public InvoiceNumber nextInvoiceNumber() {
        return new InvoiceNumber(String.format("INV-%06d", ids.next("invoice_number")));
    }

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceJpaEntity entity = repository.findById(invoice.getInvoiceId()).orElseGet(InvoiceJpaEntity::new);
        InvoiceJpaMapper.updateEntity(entity, invoice);
        repository.save(entity);
        return invoice;
    }

    @Override
    public Optional<Invoice> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(InvoiceJpaMapper::toDomain);
    }
}
