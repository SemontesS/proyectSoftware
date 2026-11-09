package application.adapters.rest.mappers;

import application.adapters.rest.dtos.shared.AddressDTO;
import application.adapters.rest.dtos.shared.MoneyDTO;
import application.adapters.rest.dtos.shared.VariantDTO;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.AddressType;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.Sku;

/** Conversions shared by every REST mapper: money, addresses, variants and enum parsing. */
public final class CommonRestMapper {

    private CommonRestMapper() {
    }

    /** Parses an enum ignoring case; an unknown value becomes an IllegalArgumentException (HTTP 400). */
    public static <E extends Enum<E>> E enumOf(Class<E> type, String value) {
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid value '" + value + "' for " + type.getSimpleName() + ".");
        }
    }

    /** Optional email: null stays null (field not being updated). */
    public static Email toEmailOrNull(String value) {
        return value == null ? null : new Email(value);
    }

    public static Money toDomain(MoneyDTO dto) {
        return Money.of(dto.amount(), enumOf(Currency.class, dto.currency()));
    }

    public static MoneyDTO toDTO(Money money) {
        return new MoneyDTO(money.amount(), money.currency().name());
    }

    public static Address toDomain(AddressDTO dto) {
        return new Address(enumOf(AddressType.class, dto.type()), dto.detail(), dto.city());
    }

    public static AddressDTO toDTO(Address address) {
        return new AddressDTO(address.type().name(), address.detail(), address.city());
    }

    public static ProductVariant toDomain(VariantDTO dto) {
        return new ProductVariant(new Sku(dto.sku()), dto.attribute(), dto.value());
    }

    public static VariantDTO toDTO(ProductVariant variant) {
        return new VariantDTO(variant.sku().value(), variant.attribute(), variant.value());
    }
}
