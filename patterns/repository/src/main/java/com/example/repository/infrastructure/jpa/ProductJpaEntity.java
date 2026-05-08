package com.example.repository.infrastructure.jpa;

import com.example.repository.domain.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
class ProductJpaEntity {

    @Id
    private String id;
    private String name;
    private String category;
    private BigDecimal price;
    private int stock;

    protected ProductJpaEntity() {}

    static ProductJpaEntity from(Product product) {
        var entity = new ProductJpaEntity();
        entity.id = product.id();
        entity.name = product.name();
        entity.category = product.category();
        entity.price = product.price();
        entity.stock = product.stock();
        return entity;
    }

    Product toDomain() {
        return new Product(id, name, category, price, stock);
    }
}
