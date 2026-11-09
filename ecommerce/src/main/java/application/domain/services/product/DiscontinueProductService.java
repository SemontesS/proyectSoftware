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

/** Permanently retires a product (owner seller or administrator). It cannot be reactivated. */
@Service
@RequiredArgsConstructor
public class DiscontinueProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Product discontinue(User requestingUser, Long productId) {
        permissions.validate(requestingUser, Permission.PRODUCT_DISCONTINUE);
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        resourceAccess.validateOwnerOrRole(requestingUser, product.getSellerId(), UserRole.ADMINISTRATOR);
        product.discontinue();
        return productRepositoryPort.save(product);
    }
}
