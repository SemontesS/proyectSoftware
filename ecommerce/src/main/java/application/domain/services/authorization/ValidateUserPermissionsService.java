package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserStatus;
import org.springframework.stereotype.Service;

/** Determines whether the role of a user allows executing an operation. */
@Service
public class ValidateUserPermissionsService {

    public void validate(User user, Permission permission) {
        if (user == null) throw new UnauthorizedOperationException("The requesting user is required.");
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedOperationException("The requesting user is not active.");
        }
        if (!permission.isAllowedFor(user.getRole())) {
            throw new UnauthorizedOperationException(
                    "Role " + user.getRole() + " is not allowed to perform " + permission + ".");
        }
    }
}
