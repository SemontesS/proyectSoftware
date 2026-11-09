package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registers that the returned goods arrived (REQUESTED -> RECEIVED). Putting them back in stock is a separate step. */
@Service
@RequiredArgsConstructor
public class ReceiveReturnService {
    private final ReturnRepositoryPort returnRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Return receive(User requestingUser, Long returnId) {
        permissions.validate(requestingUser, Permission.RETURN_RECEIVE);
        Return returnRequest = returnRepositoryPort.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
        returnRequest.receive();
        return returnRepositoryPort.save(returnRequest);
    }
}
