package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);

    @Query("select u.role, count(u) from UserJpaEntity u group by u.role")
    List<Object[]> countGroupedByRole();
}
