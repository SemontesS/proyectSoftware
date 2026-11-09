package application.domain.services.product;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Temporarily removes a published product from the catalog (owner seller or administrator). */
@Service
@RequiredArgsConstructor
public class SuspendProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Product suspend(User requestingUser, Long productId) {
        permissions.validate(requestingUser, Permission.PRODUCT_SUSPEND);
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        resourceAccess.validateOwnerOrRole(requestingUser, product.getSellerId(), UserRole.ADMINISTRATOR);
        product.suspend();
        return productRepositoryPort.save(product);
    }
}
