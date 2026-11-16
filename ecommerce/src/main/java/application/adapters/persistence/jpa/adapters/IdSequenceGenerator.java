package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues the identifiers the domain needs before an entity is saved. Each call runs in its own
 * short transaction, so the row lock on the counter is released immediately and concurrent
 * requests do not wait for each other's business transaction. Rolled-back requests leave gaps.
 */
@Component
public class IdSequenceGenerator {

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public long next(String sequenceName) {
        IdSequenceJpaEntity sequence = entityManager.find(
                IdSequenceJpaEntity.class, sequenceName, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        if (sequence == null) {
            entityManager.persist(new IdSequenceJpaEntity(sequenceName, 2L));
            entityManager.flush();
            return 1L;
        }
        long value = sequence.getNextValue();
        sequence.setNextValue(value + 1);
        return value;
    }
}
