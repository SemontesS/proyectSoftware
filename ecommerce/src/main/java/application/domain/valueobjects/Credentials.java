package application.domain.valueobjects;

import java.util.Objects;

/**
 * Authentication credentials used by the login use case.
 */
public record Credentials(String email, String password) {
    public Credentials {
        Objects.requireNonNull(email, "Email is required.");
        Objects.requireNonNull(password, "Password is required.");
        email = email.trim();
        password = password.trim();
        if (email.isBlank()) throw new IllegalArgumentException("Email is required.");
        if (password.isBlank()) throw new IllegalArgumentException("Password is required.");
    }
}
