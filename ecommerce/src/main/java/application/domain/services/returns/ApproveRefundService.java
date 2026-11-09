package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** An administrator approves the pending refund of a return; the return takes the matching final status. */
@Service
@RequiredArgsConstructor
public class ApproveRefundService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Refund approve(User requestingUser, Long returnId) {
        permissions.validate(requestingUser, Permission.REFUND_APPROVE);
        Return returnRequest = returnRepositoryPort.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
        Refund refund = returnRequest.approveRefund();
        returnRepositoryPort.save(returnRequest);
        return refund;
    }
}
