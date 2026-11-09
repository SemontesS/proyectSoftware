package application.domain.ports.out;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepositoryPort {
    Long nextId();

    Product save(Product product);

    Optional<Product> findById(Long productId);

    List<Product> findAll();

    List<Product> findByStatus(ProductStatus status);

    Map<ProductStatus, Long> countByStatus();
}
