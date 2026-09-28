package application.domain.models;

import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.UserRole;

import java.util.Objects;

/**
 * User represents a una persona que participa en la plataforma.
 * Is una class abstracta porque reúne la información común que necesitan
 * los diferentes participants (Buyer, Seller).
 *
 * Rules (Domain Model — NexusMarket, sección 4 / 21):
 * - El identificador del user debe ser único.
 * - El correo electrónico debe ser único.
 * - El documento de identidad debe ser único.
 * - Each user tiene un único role.
 * - El user opera de agreement with las responsabilidades de su role.
 */
public abstract class User {

    private final Long userId;
    private String name;
    private String email;
    private final String identityDocument;
    private final UserRole role;
    private UserStatus status;

    protected User(Long userId, String name, String email,
                       String identityDocument, UserRole role, UserStatus status) {
        this.userId = Objects.requireNonNull(userId, "userId es obligatorio");
        this.role = Objects.requireNonNull(role, "role es obligatorio");
        this.status = Objects.requireNonNull(status, "status es obligatorio");
        setNombre(name);
        setCorreoElectronico(email);
        this.identityDocument = requireNonBlank(identityDocument, "identityDocument es obligatorio");
    }

    public void setNombre(String name) {
        this.name = requireNonBlank(name, "name es obligatorio");
    }

    public void setCorreoElectronico(String email) {
        this.email = requireNonBlank(email, "email es obligatorio");
    }

    public void changeStatus(UserStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "status es obligatorio");
    }

    public Long getIdUsuario() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    private static String requireNonBlank(String value, String mensaje) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
