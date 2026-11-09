package application.domain.services.authorization;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.AuthenticationTokenPort;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.PasswordHasherPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Credentials;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticateUserService {
    private final UserRepositoryPort userRepositoryPort;
    private final CredentialsRepositoryPort credentialsRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final AuthenticationTokenPort authenticationTokenPort;

    public AuthenticationResult authenticate(Credentials credentials) {
        Objects.requireNonNull(credentials, "Credentials are required.");

        Email email = new Email(credentials.email());
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", email.value()));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("User account is not active.");
        }

        UserCredentials userCredentials = credentialsRepositoryPort.findByUserId(user.getUserId())
                .orElseThrow(() -> new BusinessRuleViolationException("User account does not have credentials configured."));

        if (userCredentials.getPasswordHash() == null || userCredentials.getPasswordHash().isBlank()) {
            throw new BusinessRuleViolationException("User account does not have credentials configured.");
        }

        if (!passwordHasherPort.matches(credentials.password(), userCredentials.getPasswordHash())) {
            throw new UnauthorizedOperationException("Invalid credentials.");
        }

        String token = authenticationTokenPort.issueFor(user);
        return new AuthenticationResult(token, user);
    }
}
