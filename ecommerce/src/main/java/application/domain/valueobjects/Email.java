package application.domain.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/** Immutable, normalized (trimmed, lower-case) and syntactically valid e-mail address. */
public record Email(String value) {
    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("Email format is invalid: " + value);
        }
    }
}
