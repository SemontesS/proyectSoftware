package application.domain.valueobjects;

import java.util.Objects;

/** Immutable address value object. */
public record Address(AddressType type, String detail, String city) {
    public Address {
        Objects.requireNonNull(type, "Address type is required.");
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("Address detail is required.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Address city is required.");
        }
        detail = detail.trim();
        city = city.trim();
    }
}
