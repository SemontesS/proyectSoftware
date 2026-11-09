package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.mappers.*;
import application.adapters.persistence.jpa.repositories.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class CredentialsRepositoryAdapter implements CredentialsRepositoryPort {
    private final CredentialsJpaRepository repository;

    @Override
    public UserCredentials save(UserCredentials credentials) {
        CredentialsJpaEntity entity = repository.findById(credentials.getUserId()).orElseGet(CredentialsJpaEntity::new);
        CredentialsJpaMapper.updateEntity(entity, credentials);
        repository.save(entity);
        return credentials;
    }

    @Override
    public Optional<UserCredentials> findByUserId(Long userId) {
        return repository.findById(userId).map(CredentialsJpaMapper::toDomain);
    }
}
