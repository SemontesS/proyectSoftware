package application.domain.ports.out;

import application.domain.models.UserCredentials;

import java.util.Optional;

public interface CredentialsRepositoryPort {
    UserCredentials save(UserCredentials credentials);

    Optional<UserCredentials> findByUserId(Long userId);
}
