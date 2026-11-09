package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class InvoiceJpaMapper {

    private InvoiceJpaMapper() {
    }

    public static Invoice toDomain(InvoiceJpaEntity entity) {
        return new Invoice(entity.getId(), entity.getOrderId(), new InvoiceNumber(entity.getNumber()),
                entity.getDate(), JpaCommonMapper.toMoney(entity.getSubtotalAmount(), entity.getCurrency()),
                JpaCommonMapper.toMoney(entity.getTotalAmount(), entity.getCurrency()));
    }

    public static void updateEntity(InvoiceJpaEntity entity, Invoice invoice) {
        entity.setId(invoice.getInvoiceId());
        entity.setOrderId(invoice.getOrderId());
        entity.setNumber(invoice.getNumber().value());
        entity.setDate(invoice.getDate());
        entity.setSubtotalAmount(invoice.getSubtotal().amount());
        entity.setTotalAmount(invoice.getTotal().amount());
        entity.setCurrency(invoice.getTotal().currency().name());
    }
}
