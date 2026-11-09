package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/** Activates or blocks a user account. Only administrators. */
@Service
@RequiredArgsConstructor
public class ChangeUserStatusService {
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public User change(User requestingUser, Long userId, UserStatus status) {
        permissions.validate(requestingUser, Permission.USER_CHANGE_STATUS);
        Objects.requireNonNull(status, "User status is required.");
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        user.changeStatus(status);
        return userRepositoryPort.save(user);
    }
}
