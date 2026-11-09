package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
public class CredentialsJpaEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Version
    private Long version;
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;
    @Column(name = "token_version", nullable = false)
    private int tokenVersion;
}
