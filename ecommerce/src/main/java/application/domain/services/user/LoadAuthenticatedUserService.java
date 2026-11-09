package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.JwtServicePort.TokenClaims;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves the user behind a token on every request: the signature must be valid, the token
 * version must be the current one (no logout / password change since) and the account active.
 */
@Service
@RequiredArgsConstructor
public class LoadAuthenticatedUserService {
    private final JwtServicePort jwtServicePort;
    private final UserRepositoryPort userRepositoryPort;
    private final CredentialsRepositoryPort credentialsRepositoryPort;

    public User load(String token) {
        TokenClaims claims = jwtServicePort.parseToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired token."));
        UserCredentials credentials = credentialsRepositoryPort.findByUserId(claims.userId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired token."));
        if (credentials.getTokenVersion() != claims.tokenVersion()) {
            throw new InvalidCredentialsException("The token is no longer valid.");
        }
        User user = userRepositoryPort.findById(claims.userId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired token."));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedOperationException("The user account is not active.");
        }
        return user;
    }
}
