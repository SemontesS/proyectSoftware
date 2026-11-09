package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** A user consults its own data; administrators and supervisors consult any user. */
@Service
@RequiredArgsConstructor
public class ConsultUserService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public User consult(User requestingUser, Long userId) {
        permissions.validate(requestingUser, Permission.USER_CONSULT);
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        resourceAccess.validateOwnerOrRole(requestingUser, user.getUserId(),
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return user;
    }
}
