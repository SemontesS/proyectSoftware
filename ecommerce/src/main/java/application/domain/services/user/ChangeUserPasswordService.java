package application.domain.services.user;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RawPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** A user changes its own password (the current one is required). Previous tokens stop working. */
@Service
@RequiredArgsConstructor
public class ChangeUserPasswordService {
    private final CredentialsRepositoryPort credentialsRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final ValidateUserPermissionsService permissions;

    public void change(User requestingUser, RawPassword currentPassword, RawPassword newPassword) {
        permissions.validate(requestingUser, Permission.USER_UPDATE);
        UserCredentials credentials = credentialsRepositoryPort.findByUserId(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Credentials", requestingUser.getUserId()));
        if (!passwordServicePort.matches(currentPassword, credentials.getPasswordHash())) {
            throw new UnauthorizedOperationException("The current password is incorrect.");
        }
        if (currentPassword.equals(newPassword)) {
            throw new BusinessRuleViolationException("The new password must differ from the current one.");
        }
        credentials.changePasswordHash(passwordServicePort.encrypt(newPassword));
        credentialsRepositoryPort.save(credentials);
    }
}
