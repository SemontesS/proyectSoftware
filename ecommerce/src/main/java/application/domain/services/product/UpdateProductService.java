package application.domain.services.product;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateResourceAccessService;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** The owner seller updates name and/or price. Null arguments mean "no change". Discontinued products are frozen. */
@Service
@RequiredArgsConstructor
public class UpdateProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateUserPermissionsService permissions;
    private final ValidateResourceAccessService resourceAccess;

    public Product update(User requestingUser, Long productId, String name, Money price) {
        permissions.validate(requestingUser, Permission.PRODUCT_UPDATE);
        if (name == null && price == null) throw new IllegalArgumentException("Nothing to update.");
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        resourceAccess.validateOwnerOrRole(requestingUser, product.getSellerId());

        if (name != null) product.changeProductName(name);
        if (price != null) product.updatePrice(price);
        return productRepositoryPort.save(product);
    }
}
