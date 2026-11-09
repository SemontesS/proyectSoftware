package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.ports.out.CredentialsRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Invalidates every token issued to the user so far. */
@Service
@RequiredArgsConstructor
public class LogoutService {
    private final CredentialsRepositoryPort credentialsRepositoryPort;

    public void logout(User requestingUser) {
        UserCredentials credentials = credentialsRepositoryPort.findByUserId(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Credentials", requestingUser.getUserId()));
        credentials.invalidateTokens();
        credentialsRepositoryPort.save(credentials);
    }
}
