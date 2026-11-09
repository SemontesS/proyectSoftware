package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.RefundResponseDTO;
import application.adapters.rest.dtos.responses.ReturnResponseDTO;
import application.domain.models.Refund;
import application.domain.models.Return;

public final class ReturnRestMapper {

    private ReturnRestMapper() {
    }

    public static ReturnResponseDTO toReturnDTO(Return returnRequest) {
        return new ReturnResponseDTO(returnRequest.getReturnId(), returnRequest.getOrderId(),
                returnRequest.getReason(), returnRequest.getDate(), returnRequest.getStatus().name(),
                returnRequest.getRefund().map(ReturnRestMapper::toRefundDTO).orElse(null));
    }

    public static RefundResponseDTO toRefundDTO(Refund refund) {
        return new RefundResponseDTO(refund.getRefundId(), refund.getReturnId(),
                CommonRestMapper.toDTO(refund.getAmount()), refund.getDate(), refund.getStatus().name());
    }
}
