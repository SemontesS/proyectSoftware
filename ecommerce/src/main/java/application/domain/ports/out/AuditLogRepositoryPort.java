package application.domain.ports.out;

import application.domain.models.AuditLog;

import java.util.List;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByEntity(String entityType, Long entityId);
}
