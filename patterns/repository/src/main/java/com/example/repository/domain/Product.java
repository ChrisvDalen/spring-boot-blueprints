package com.example.repository.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Pure domain object — no JPA annotations, no framework dependencies.
 * The repository pattern keeps this clean by acting as the mapping boundary.
 */
public record Product(
        String id,
        String name,
        String category,
        BigDecimal price,
        int stock
) {
    public static Product create(String name, String category, BigDecimal price, int stock) {
        return new Product(UUID.randomUUID().toString(), name, category, price, stock);
    }

    public Product withReducedStock(int quantity) {
        if (quantity > this.stock) {
            throw new IllegalArgumentException("Insufficient stock: have %d, need %d".formatted(this.stock, quantity));
        }
        return new Product(id, name, category, price, this.stock - quantity);
    }
}
