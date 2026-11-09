package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;

import java.util.List;

public final class ReturnJpaMapper {

    private ReturnJpaMapper() {
    }

    public static Return toDomain(ReturnJpaEntity entity) {
        Refund refund = entity.getRefundId() == null ? null
                : new Refund(entity.getRefundId(), entity.getId(),
                        JpaCommonMapper.toMoney(entity.getRefundAmount(), entity.getRefundCurrency()),
                        entity.getRefundDate(), RefundStatus.valueOf(entity.getRefundStatus()));
        return Return.restore(entity.getId(), entity.getOrderId(), entity.getReason(), entity.getDate(),
                ReturnStatus.valueOf(entity.getStatus()), refund);
    }

    public static void updateEntity(ReturnJpaEntity entity, Return returnRequest) {
        entity.setId(returnRequest.getReturnId());
        entity.setOrderId(returnRequest.getOrderId());
        entity.setReason(returnRequest.getReason());
        entity.setDate(returnRequest.getDate());
        entity.setStatus(returnRequest.getStatus().name());
        Refund refund = returnRequest.getRefund().orElse(null);
        entity.setRefundId(refund == null ? null : refund.getRefundId());
        entity.setRefundAmount(refund == null ? null : refund.getAmount().amount());
        entity.setRefundCurrency(refund == null ? null : refund.getAmount().currency().name());
        entity.setRefundDate(refund == null ? null : refund.getDate());
        entity.setRefundStatus(refund == null ? null : refund.getStatus().name());
    }
}
