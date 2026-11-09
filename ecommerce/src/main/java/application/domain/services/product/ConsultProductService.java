package application.domain.services.product;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Visibility: buyers see only PUBLISHED products; a seller sees its own products in any status
 * plus the published ones of other sellers; staff roles see everything.
 */
@Service
@RequiredArgsConstructor
public class ConsultProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Product consult(User requestingUser, Long productId) {
        permissions.validate(requestingUser, Permission.PRODUCT_CONSULT);
        return productRepositoryPort.findById(productId)
                .filter(product -> isVisibleTo(requestingUser, product))
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    public List<Product> consultCatalog(User requestingUser) {
        permissions.validate(requestingUser, Permission.PRODUCT_CONSULT);
        if (requestingUser.getRole() == UserRole.BUYER) {
            return productRepositoryPort.findByStatus(ProductStatus.PUBLISHED);
        }
        return productRepositoryPort.findAll().stream()
                .filter(product -> isVisibleTo(requestingUser, product))
                .toList();
    }

    private boolean isVisibleTo(User user, Product product) {
        return switch (user.getRole()) {
            case BUYER -> product.isPublished();
            case SELLER -> product.isPublished() || product.getSellerId().equals(user.getUserId());
            default -> true;
        };
    }
}
