package application.adapters.persistence.mongodb;

import application.domain.models.AuditLog;
import application.domain.valueobjects.AuditEntityType;
import application.domain.valueobjects.Permission;

public final class AuditLogMongoMapper {

    private AuditLogMongoMapper() {
    }

    public static AuditLog toDomain(AuditLogDocument document) {
        return new AuditLog(document.getId(), document.getUserId(), Permission.valueOf(document.getAction()),
                AuditEntityType.valueOf(document.getEntityType()), document.getEntityId(), document.getDetail(), document.getTimestamp());
    }

    public static AuditLogDocument toDocument(AuditLog log) {
        AuditLogDocument document = new AuditLogDocument();
        document.setId(log.auditId());
        document.setUserId(log.userId());
        document.setAction(log.action().name());
        document.setEntityType(log.entityType().name());
        document.setEntityId(log.entityId());
        document.setDetail(log.detail());
        document.setTimestamp(log.timestamp());
        return document;
    }
}
