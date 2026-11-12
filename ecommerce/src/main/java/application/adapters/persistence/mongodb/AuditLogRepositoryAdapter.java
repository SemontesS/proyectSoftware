package application.adapters.persistence.mongodb;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** Output adapter: audit trail on MongoDB. It is not part of the MySQL transaction. */
@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {
    private final AuditLogMongoRepository repository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return AuditLogMongoMapper.toDomain(repository.save(AuditLogMongoMapper.toDocument(auditLog)));
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return repository.findByUserIdOrderByTimestampAsc(userId).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByEntity(AuditEntityType entityType, Long entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByTimestampAsc(entityType.name(), entityId).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }
}
