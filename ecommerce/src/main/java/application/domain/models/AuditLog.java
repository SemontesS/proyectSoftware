package application.domain.models;

import application.domain.valueobjects.AuditEntityType;
import application.domain.valueobjects.Permission;

import java.time.LocalDateTime;
import java.util.Objects;

/** Immutable audit entry describing a business operation executed by a user. */
public record AuditLog(String auditId, Long userId, Permission action, AuditEntityType entityType,
                       Long entityId, String detail, LocalDateTime timestamp) {
    public AuditLog {
        Objects.requireNonNull(userId, "User ID is required.");
        Objects.requireNonNull(action, "Audit action is required.");
        Objects.requireNonNull(entityType, "Audit entity type is required.");
        Objects.requireNonNull(timestamp, "Audit timestamp is required.");
    }
}
