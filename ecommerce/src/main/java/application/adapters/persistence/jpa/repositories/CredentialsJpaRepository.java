package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsJpaRepository extends JpaRepository<CredentialsJpaEntity, Long> {
}
