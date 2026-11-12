package application.domain.ports.out;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.AuditLog;

import java.util.List;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByEntity(AuditEntityType entityType, Long entityId);
}
