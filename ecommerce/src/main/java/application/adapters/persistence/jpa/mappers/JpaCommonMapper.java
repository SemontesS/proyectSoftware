package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

/** Conversions shared by the JPA mappers. */
public final class JpaCommonMapper {

    private JpaCommonMapper() {
    }

    public static AddressEmbeddable toEmbeddable(Address address) {
        return address == null ? null
                : new AddressEmbeddable(address.type().name(), address.detail(), address.city());
    }

    public static Address toDomain(AddressEmbeddable embeddable) {
        return embeddable == null ? null
                : new Address(AddressType.valueOf(embeddable.getType()), embeddable.getDetail(), embeddable.getCity());
    }

    public static Money toMoney(java.math.BigDecimal amount, String currency) {
        return Money.of(amount, Currency.valueOf(currency));
    }
}
