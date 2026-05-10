package com.example.repository.infrastructure.inmemory;

import com.example.repository.domain.Product;
import com.example.repository.domain.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ProductRepository.
 *
 * Demonstrates that the same domain interface can have multiple implementations.
 * Used in tests to avoid a database, or as a lightweight development stub.
 *
 * The domain and application code never know which implementation is active —
 * they depend only on the ProductRepository interface.
 */
@Repository("inMemoryProductRepository")
public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> store = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        store.put(product.id(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return store.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category))
                .toList();
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        return store.values().stream()
                .filter(p -> p.stock() <= threshold)
                .toList();
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }
}
