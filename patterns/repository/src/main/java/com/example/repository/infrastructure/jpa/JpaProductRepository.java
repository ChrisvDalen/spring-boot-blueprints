package com.example.repository.infrastructure.jpa;

import com.example.repository.domain.Product;
import com.example.repository.domain.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the domain ProductRepository port.
 * All JPA knowledge — entities, JPQL, mapping — is contained here.
 * Swap this for a MongoDB implementation and nothing in domain or application changes.
 */
@Repository
@Primary
public class JpaProductRepository implements ProductRepository {

    private final ProductJpaRepository jpa;

    public JpaProductRepository(ProductJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Product save(Product product) {
        return jpa.save(ProductJpaEntity.from(product)).toDomain();
    }

    @Override
    public Optional<Product> findById(String id) {
        return jpa.findById(id).map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAll().stream().map(ProductJpaEntity::toDomain).toList();
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpa.findByCategory(category).stream().map(ProductJpaEntity::toDomain).toList();
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        return jpa.findByStockLessThanEqual(threshold).stream().map(ProductJpaEntity::toDomain).toList();
    }

    @Override
    public void delete(String id) {
        jpa.deleteById(id);
    }
}
