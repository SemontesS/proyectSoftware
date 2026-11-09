package application.domain.services.authorization;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

/** Determines whether a user may access a specific resource (ownership or privileged role). */
@Service
@RequiredArgsConstructor
public class ValidateResourceAccessService {
    private final ProductRepositoryPort productRepositoryPort;

    public boolean hasAnyRole(User user, UserRole... roles) {
        return Arrays.asList(roles).contains(user.getRole());
    }

    /** The user must be the owner of the resource or hold one of the privileged roles. */
    public void validateOwnerOrRole(User user, Long ownerUserId, UserRole... privilegedRoles) {
        if (hasAnyRole(user, privilegedRoles)) return;
        if (!Objects.equals(user.getUserId(), ownerUserId)) {
            throw new UnauthorizedOperationException("The user cannot access this resource.");
        }
    }

    /** Sellers only reach the inventory of their own products; staff roles reach all of it. */
    public void validateInventoryAccess(User user, Inventory inventory) {
        if (hasAnyRole(user, UserRole.ADMINISTRATOR, UserRole.LOGISTICS_OPERATOR, UserRole.SUPERVISOR)) return;
        Product product = productRepositoryPort.findById(inventory.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product", inventory.getProductId()));
        validateOwnerOrRole(user, product.getSellerId());
    }
}
