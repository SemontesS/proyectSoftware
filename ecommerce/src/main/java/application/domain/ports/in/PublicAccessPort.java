package application.domain.ports.in;

import application.domain.models.*;
import application.domain.valueobjects.*;

import java.util.List;

/** Operations available without authentication, plus logout for the authenticated user. */
public interface PublicAccessPort {
    AuthenticationResult login(Email email, RawPassword password);

    void logout(User requestingUser);

    Buyer registerBuyer(String name, Email email, IdentityDocument identityDocument,
                        Address primaryAddress, RawPassword password);
}
