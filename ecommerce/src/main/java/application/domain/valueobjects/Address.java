package application.domain.valueobjects;

import java.util.Objects;

/** Immutable address value object. */
public record Address(String type, String detail, String city) {
    public Address {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Address type is required.");
        }
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("Address detail is required.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Address city is required.");
        }
        type = type.trim();
        detail = detail.trim();
        city = city.trim();
    }
}
