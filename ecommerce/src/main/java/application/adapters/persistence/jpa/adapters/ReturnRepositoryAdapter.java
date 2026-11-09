package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.mappers.*;
import application.adapters.persistence.jpa.repositories.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class ReturnRepositoryAdapter implements ReturnRepositoryPort {
    private final ReturnJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("returns");
    }

    @Override
    public Long nextRefundId() {
        return ids.next("refunds");
    }

    @Override
    public Return save(Return returnRequest) {
        ReturnJpaEntity entity = repository.findById(returnRequest.getReturnId()).orElseGet(ReturnJpaEntity::new);
        ReturnJpaMapper.updateEntity(entity, returnRequest);
        repository.save(entity);
        return returnRequest;
    }

    @Override
    public Optional<Return> findById(Long returnId) {
        return repository.findById(returnId).map(ReturnJpaMapper::toDomain);
    }

    @Override
    public List<Return> findByOrderId(Long orderId) {
        return repository.findByOrderIdOrderByIdAsc(orderId).stream().map(ReturnJpaMapper::toDomain).toList();
    }
}
