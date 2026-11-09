package application.adapters.persistence.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/** Audit entry stored in MongoDB (append-only collection). */
@Document(collection = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogDocument {
    @Id
    private String id;
    private Long userId;
    private String action;
    private String entityType;
    private Long entityId;
    private String detail;
    private LocalDateTime timestamp;
}
