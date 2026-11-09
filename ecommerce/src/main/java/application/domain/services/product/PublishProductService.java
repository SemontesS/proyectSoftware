package application.domain.services.product;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Publishes a product in the catalog (owner seller or administrator; the seller must be active). */
@Service
@RequiredArgsConstructor
public class PublishProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Product publish(User requestingUser, Long productId) {
        permissions.validate(requestingUser, Permission.PRODUCT_PUBLISH);
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        resourceAccess.validateOwnerOrRole(requestingUser, product.getSellerId(), UserRole.ADMINISTRATOR);

        Seller seller = userRepositoryPort.findSellerById(product.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException("Seller", product.getSellerId()));
        if (seller.getSellerStatus() != SellerStatus.ACTIVE) {
            throw new BusinessRuleViolationException("A suspended seller cannot publish products.");
        }
        product.publish();
        return productRepositoryPort.save(product);
    }
}
