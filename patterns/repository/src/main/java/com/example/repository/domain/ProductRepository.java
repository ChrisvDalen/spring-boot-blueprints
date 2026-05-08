package com.example.repository.domain;

import java.util.List;
import java.util.Optional;

/**
 * The repository interface is defined in the DOMAIN package — not in infrastructure.
 *
 * This is the core of the pattern: the domain states what it needs,
 * and infrastructure provides it. Dependency arrows point inward.
 *
 * The interface speaks domain language (findByCategory, findLowStock),
 * not SQL or JPA language. Callers never see "SELECT" or "EntityManager".
 */
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findLowStock(int threshold);
    void delete(String id);
}
