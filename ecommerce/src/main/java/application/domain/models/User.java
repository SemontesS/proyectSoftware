package application.domain.models;

import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/** Base entity for people participating in the marketplace. */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class User {
    @EqualsAndHashCode.Include
    private final Long userId;
    private String name;
    private String email;
    private final String identityDocument;
    private final UserRole role;
    private UserStatus status;

    protected User(Long userId, String name, String email, String identityDocument,
                   UserRole role, UserStatus status) {
        this.userId = Objects.requireNonNull(userId, "User ID is required.");
        this.role = Objects.requireNonNull(role, "User role is required.");
        this.status = Objects.requireNonNull(status, "User status is required.");
        this.name = requireNonBlank(name, "User name is required.");
        this.email = requireNonBlank(email, "User email is required.");
        this.identityDocument = requireNonBlank(identityDocument, "Identity document is required.");
    }

    public void changeName(String name) {
        this.name = requireNonBlank(name, "User name is required.");
    }

    public void changeEmail(String email) {
        this.email = requireNonBlank(email, "User email is required.");
    }

    public void changeStatus(UserStatus status) {
        this.status = Objects.requireNonNull(status, "User status is required.");
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
