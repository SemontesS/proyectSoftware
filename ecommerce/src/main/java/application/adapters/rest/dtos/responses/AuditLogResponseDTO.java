package application.adapters.rest.dtos.responses;


import java.time.LocalDateTime;

public record AuditLogResponseDTO(String auditId, Long userId, String action, String entityType,
        Long entityId, String detail, LocalDateTime timestamp) {}
