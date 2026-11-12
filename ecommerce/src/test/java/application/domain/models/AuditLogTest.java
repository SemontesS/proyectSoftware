package application.domain.models;

import application.domain.valueobjects.AuditEntityType;
import application.domain.valueobjects.Permission;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuditLogTest {

    @Test
    void keepsTheTypedEntityType() {
        AuditLog log = new AuditLog(null, 1L, Permission.USER_REGISTER, AuditEntityType.USER, 1L,
                "Self-registration", LocalDateTime.now());
        assertEquals(AuditEntityType.USER, log.entityType());
    }

    @Test
    void entityTypeIsRequired() {
        assertThrows(NullPointerException.class, () -> new AuditLog(null, 1L, Permission.USER_REGISTER, null, 1L,
                "detail", LocalDateTime.now()));
    }
}
