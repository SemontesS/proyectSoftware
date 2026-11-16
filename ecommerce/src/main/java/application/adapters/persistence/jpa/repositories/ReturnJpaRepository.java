package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnJpaRepository extends JpaRepository<ReturnJpaEntity, Long> {
    List<ReturnJpaEntity> findByOrderIdOrderByIdAsc(Long orderId);
}
