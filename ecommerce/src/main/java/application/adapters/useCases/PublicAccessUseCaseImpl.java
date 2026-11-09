package application.adapters.useCases;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.audit.RecordAuditService;
import application.domain.services.buyer.RegisterBuyerService;
import application.domain.services.user.LoginService;
import application.domain.services.user.LogoutService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.RawPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PublicAccessUseCaseImpl implements PublicAccessPort {
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterBuyerService registerBuyerService;
    private final RecordAuditService audit;

    @Override
    public AuthenticationResult login(Email email, RawPassword password) {
        return loginService.login(email, password);
    }

    @Override
    public void logout(User requestingUser) {
        logoutService.logout(requestingUser);
    }

    @Override
    public Buyer registerBuyer(String name, Email email, IdentityDocument identityDocument,
                               Address primaryAddress, RawPassword password) {
        Buyer buyer = registerBuyerService.register(name, email, identityDocument, primaryAddress, password);
        audit.record(buyer, Permission.USER_REGISTER, "Buyer", buyer.getUserId(), "Self-registration");
        return buyer;
    }
}
