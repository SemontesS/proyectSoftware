package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReturnJpaRepository extends JpaRepository<ReturnJpaEntity, Long> {
    List<ReturnJpaEntity> findByOrderIdOrderByIdAsc(Long orderId);
}
