package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Authenticates a user by email and password and issues a token. Failures never reveal which part was wrong. */
@Service
@RequiredArgsConstructor
public class LoginService {
    private static final String INVALID_CREDENTIALS = "Invalid credentials.";

    private final UserRepositoryPort userRepositoryPort;
    private final CredentialsRepositoryPort credentialsRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final JwtServicePort jwtServicePort;

    public AuthenticationResult login(Email email, RawPassword password) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));
        UserCredentials credentials = credentialsRepositoryPort.findByUserId(user.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));
        if (!passwordServicePort.matches(password, credentials.getPasswordHash())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedOperationException("The user account is not active.");
        }
        return new AuthenticationResult(jwtServicePort.generateToken(user, credentials.getTokenVersion()), user);
    }
}
