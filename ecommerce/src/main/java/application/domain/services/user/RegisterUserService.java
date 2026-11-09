package application.domain.services.user;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.models.InternalUser;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.valueobjects.RawPassword;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registers marketplace staff (administrator, logistics operator, supervisor). Email and document are unique. */
@Service
@RequiredArgsConstructor
public class RegisterUserService {
    private final UserRepositoryPort userRepositoryPort;
    private final CredentialsRepositoryPort credentialsRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final ValidateUserPermissionsService permissions;

    public InternalUser register(User requestingUser, String name, Email email,
                                 IdentityDocument identityDocument, UserRole role, RawPassword password) {
        permissions.validate(requestingUser, Permission.USER_REGISTER);
        if (userRepositoryPort.existsByEmail(email)) {
            throw new DuplicateEntityException("The email is already registered: " + email.value());
        }
        if (userRepositoryPort.existsByIdentityDocument(identityDocument)) {
            throw new DuplicateEntityException("The identity document is already registered.");
        }
        InternalUser user = new InternalUser(userRepositoryPort.nextId(), name, email,
                identityDocument, role, UserStatus.ACTIVE);
        InternalUser saved = userRepositoryPort.save(user);
        credentialsRepositoryPort.save(UserCredentials.create(saved.getUserId(), passwordServicePort.encrypt(password)));
        return saved;
    }
}
