package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.mappers.*;
import application.adapters.persistence.jpa.repositories.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.*;
import application.domain.valueobjects.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository repository;
    private final IdSequenceGenerator ids;

    @Override
    public Long nextId() {
        return ids.next("products");
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = repository.findById(product.getProductId()).orElseGet(ProductJpaEntity::new);
        ProductJpaMapper.updateEntity(entity, product);
        repository.save(entity);
        return product;
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return repository.findById(productId).map(ProductJpaMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return repository.findByStatus(status.name()).stream().map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public Map<ProductStatus, Long> countByStatus() {
        Map<ProductStatus, Long> counts = new EnumMap<>(ProductStatus.class);
        for (Object[] row : repository.countGroupedByStatus()) {
            counts.put(ProductStatus.valueOf((String) row[0]), (Long) row[1]);
        }
        return counts;
    }
}
