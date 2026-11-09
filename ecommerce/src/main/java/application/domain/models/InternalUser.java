package application.domain.models;

import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import lombok.EqualsAndHashCode;

/** Marketplace staff: administrator, logistics operator or supervisor. */
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class InternalUser extends User {

    public InternalUser(Long userId, String name, Email email, IdentityDocument identityDocument,
                        UserRole role, UserStatus status) {
        super(userId, name, email, identityDocument, role, status);
        if (role == UserRole.BUYER || role == UserRole.SELLER) {
            throw new IllegalArgumentException("An internal user cannot have the role " + role + ".");
        }
    }
}
