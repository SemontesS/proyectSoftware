package application.domain.services.audit;

import application.domain.valueobjects.AuditEntityType;
import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/** Records that a user executed a business operation. The operation is identified by its Permission. */
@Service
@RequiredArgsConstructor
public class RecordAuditService {
    private final AuditLogRepositoryPort auditLogRepositoryPort;
    private final Clock clock;

    public AuditLog record(User user, Permission action, AuditEntityType entityType, Long entityId, String detail) {
        return auditLogRepositoryPort.save(new AuditLog(null, user.getUserId(), action, entityType,
                entityId, detail, LocalDateTime.now(clock)));
    }
}
