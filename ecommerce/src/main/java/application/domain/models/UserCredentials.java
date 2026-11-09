package application.domain.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Login credentials of a user, kept apart from the User entity. The token version is stored in
 * every issued JWT; incrementing it invalidates all previously issued tokens (logout, password change).
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserCredentials {
    @EqualsAndHashCode.Include
    private final Long userId;
    private String passwordHash;
    private int tokenVersion;

    public UserCredentials(Long userId, String passwordHash, int tokenVersion) {
        this.userId = Objects.requireNonNull(userId, "User ID is required.");
        this.passwordHash = requireHash(passwordHash);
        if (tokenVersion < 1) throw new IllegalArgumentException("Token version must be at least 1.");
        this.tokenVersion = tokenVersion;
    }

    public static UserCredentials create(Long userId, String passwordHash) {
        return new UserCredentials(userId, passwordHash, 1);
    }

    /** Replaces the password and invalidates every token issued so far. */
    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = requireHash(newPasswordHash);
        invalidateTokens();
    }

    public void invalidateTokens() {
        tokenVersion++;
    }

    private static String requireHash(String hash) {
        if (hash == null || hash.isBlank()) throw new IllegalArgumentException("Password hash is required.");
        return hash;
    }
}
