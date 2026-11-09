package application.domain.services.buyer;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.models.Buyer;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Public self-registration of a buyer: no requesting user. Email and document are unique. */
@Service
@RequiredArgsConstructor
public class RegisterBuyerService {
    private final UserRepositoryPort userRepositoryPort;

    public Buyer register(String name, Email email, IdentityDocument identityDocument, Address primaryAddress) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new DuplicateEntityException("The email is already registered: " + email.value());
        }
        if (userRepositoryPort.existsByIdentityDocument(identityDocument)) {
            throw new DuplicateEntityException("The identity document is already registered.");
        }
        Buyer buyer = new Buyer(userRepositoryPort.nextId(), name, email, identityDocument,
                UserStatus.ACTIVE, primaryAddress, BuyerStatus.ENABLED);
        return userRepositoryPort.save(buyer);
    }
}
