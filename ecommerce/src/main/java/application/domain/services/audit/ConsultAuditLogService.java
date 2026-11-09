package application.domain.services.audit;

import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Administrators and supervisors consult the audit trail by user or by affected entity. */
@Service
@RequiredArgsConstructor
public class ConsultAuditLogService {
    private final AuditLogRepositoryPort auditLogRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public List<AuditLog> consultByUser(User requestingUser, Long userId) {
        permissions.validate(requestingUser, Permission.AUDIT_CONSULT);
        return auditLogRepositoryPort.findByUserId(userId);
    }

    public List<AuditLog> consultByEntity(User requestingUser, String entityType, Long entityId) {
        permissions.validate(requestingUser, Permission.AUDIT_CONSULT);
        return auditLogRepositoryPort.findByEntity(entityType, entityId);
    }
}
