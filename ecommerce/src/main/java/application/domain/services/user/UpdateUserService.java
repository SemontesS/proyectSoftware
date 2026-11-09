package application.domain.services.user;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Updates the name and/or email of a user. Null arguments mean "no change". */
@Service
@RequiredArgsConstructor
public class UpdateUserService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public User update(User requestingUser, Long userId, String name, Email email) {
        permissions.validate(requestingUser, Permission.USER_UPDATE);
        if (name == null && email == null) throw new IllegalArgumentException("Nothing to update.");
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        resourceAccess.validateOwnerOrRole(requestingUser, user.getUserId(), UserRole.ADMINISTRATOR);

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepositoryPort.existsByEmail(email)) {
                throw new DuplicateEntityException("The email is already registered: " + email.value());
            }
            user.changeEmail(email);
        }
        if (name != null) user.changeName(name);
        return userRepositoryPort.save(user);
    }
}
