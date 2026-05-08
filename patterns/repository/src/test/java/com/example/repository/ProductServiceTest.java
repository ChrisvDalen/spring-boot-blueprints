package com.example.repository;

import com.example.repository.application.ProductService;
import com.example.repository.domain.Product;
import com.example.repository.infrastructure.inmemory.InMemoryProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the service against the InMemoryProductRepository — no Spring, no database.
 * This is possible only because the domain defines the repository interface,
 * and the service depends on that interface, not on JPA.
 */
class ProductServiceTest {

    private final InMemoryProductRepository repo = new InMemoryProductRepository();
    private final ProductService service = new ProductService(repo);

    @Test
    void create_and_findById() {
        Product created = service.create("Lightsaber", "WEAPONS", new BigDecimal("299.99"), 50);
        assertThat(service.findById(created.id())).isPresent().get().isEqualTo(created);
    }

    @Test
    void findByCategory_returnsOnlyMatching() {
        service.create("Lightsaber", "WEAPONS", new BigDecimal("299.99"), 50);
        service.create("R2-D2", "DROIDS", new BigDecimal("4999.00"), 5);

        assertThat(service.findByCategory("WEAPONS")).hasSize(1);
        assertThat(service.findByCategory("DROIDS")).hasSize(1);
        assertThat(service.findByCategory("FOOD")).isEmpty();
    }

    @Test
    void findLowStock_returnsItemsBelowThreshold() {
        service.create("Scarce Item", "RARE", new BigDecimal("9999.00"), 2);
        service.create("Plentiful Item", "COMMON", new BigDecimal("1.00"), 1000);

        assertThat(service.findLowStock(10)).hasSize(1)
                .first().extracting(Product::name).isEqualTo("Scarce Item");
    }

    @Test
    void reduceStock_insufficientStock_throws() {
        Product product = service.create("Limited", "RARE", new BigDecimal("500.00"), 3);
        assertThatThrownBy(() -> service.reduceStock(product.id(), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void reduceStock_updatesCorrectly() {
        Product product = service.create("Widget", "PARTS", new BigDecimal("10.00"), 20);
        Product reduced = service.reduceStock(product.id(), 5);
        assertThat(reduced.stock()).isEqualTo(15);
    }
}
