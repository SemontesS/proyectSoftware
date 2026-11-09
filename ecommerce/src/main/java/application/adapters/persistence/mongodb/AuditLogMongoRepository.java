package application.adapters.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogMongoRepository extends MongoRepository<AuditLogDocument, String> {
    List<AuditLogDocument> findByUserIdOrderByTimestampAsc(Long userId);

    List<AuditLogDocument> findByEntityTypeAndEntityIdOrderByTimestampAsc(String entityType, Long entityId);
}
