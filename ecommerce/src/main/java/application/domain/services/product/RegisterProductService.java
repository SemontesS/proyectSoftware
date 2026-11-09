package application.domain.services.product;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.Permission;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SellerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * An active seller registers a product with at least one variant. The product is created
 * SUSPENDED (not visible) until PublishProductService publishes it.
 */
@Service
@RequiredArgsConstructor
public class RegisterProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public Product register(User requestingUser, String name, ProductType type, Money price,
                            List<ProductVariant> variants) {
        permissions.validate(requestingUser, Permission.PRODUCT_REGISTER);
        if (variants == null || variants.isEmpty()) {
            throw new BusinessRuleViolationException("A product needs at least one variant (SKU).");
        }
        Seller seller = userRepositoryPort.findSellerById(requestingUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Seller", requestingUser.getUserId()));
        if (seller.getSellerStatus() != SellerStatus.ACTIVE) {
            throw new BusinessRuleViolationException("A suspended seller cannot register products.");
        }
        Product product = new Product(productRepositoryPort.nextId(), seller.getUserId(), name, type,
                ProductStatus.SUSPENDED, price);
        variants.forEach(product::addVariant);
        return productRepositoryPort.save(product);
    }
}
