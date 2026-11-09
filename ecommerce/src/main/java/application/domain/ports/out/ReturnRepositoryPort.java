package application.domain.ports.out;

import application.domain.models.Return;

import java.util.List;
import java.util.Optional;

public interface ReturnRepositoryPort {
    Long nextId();

    Long nextRefundId();

    Return save(Return returnRequest);

    Optional<Return> findById(Long returnId);

    List<Return> findByOrderId(Long orderId);
}
