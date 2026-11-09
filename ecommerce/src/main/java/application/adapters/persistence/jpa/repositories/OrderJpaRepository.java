package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
    List<OrderJpaEntity> findByBuyerIdOrderByIdAsc(Long buyerId);

    @Query("select o.status, count(o) from OrderJpaEntity o group by o.status")
    List<Object[]> countGroupedByStatus();

    /** Total sold per currency: paid orders only (not pending payment, not cancelled). */
    @Query("select o.currency, sum(l.unitAmount * l.quantity) from OrderJpaEntity o join o.lines l "
            + "where o.status not in ('PENDING_PAYMENT', 'CANCELLED') group by o.currency")
    List<Object[]> salesTotalsGroupedByCurrency();
}
