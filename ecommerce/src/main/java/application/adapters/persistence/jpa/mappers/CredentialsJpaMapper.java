package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class CredentialsJpaMapper {

    private CredentialsJpaMapper() {
    }

    public static UserCredentials toDomain(CredentialsJpaEntity entity) {
        return new UserCredentials(entity.getUserId(), entity.getPasswordHash(), entity.getTokenVersion());
    }

    public static void updateEntity(CredentialsJpaEntity entity, UserCredentials credentials) {
        entity.setUserId(credentials.getUserId());
        entity.setPasswordHash(credentials.getPasswordHash());
        entity.setTokenVersion(credentials.getTokenVersion());
    }
}
