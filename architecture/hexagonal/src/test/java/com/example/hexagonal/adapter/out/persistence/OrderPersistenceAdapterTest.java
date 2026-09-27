package com.example.hexagonal.adapter.out.persistence;

import com.example.hexagonal.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence-adapter tests: verifies that the domain Order survives the
 * domain -> JPA entity -> database -> entity -> domain round trip.
 */
@DataJpaTest
class OrderPersistenceAdapterTest {

    @Autowired OrderJpaRepository jpaRepository;

    private OrderPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        adapter = new OrderPersistenceAdapter(jpaRepository);
    }

    @Test
    void saveThenFindById_roundTripsAllFields() {
        Order domain = new Order("luke@jedi.org", "LIGHTSABER", 3, new BigDecimal("299.99"));

        Order saved = adapter.save(domain);
        Optional<Order> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        Order reloaded = found.get();
        assertThat(reloaded.getId()).isEqualTo(saved.getId());
        assertThat(reloaded.getCustomerEmail()).isEqualTo("luke@jedi.org");
        assertThat(reloaded.getProductSku()).isEqualTo("LIGHTSABER");
        assertThat(reloaded.getQuantity()).isEqualTo(3);
        assertThat(reloaded.getTotalPrice()).isEqualByComparingTo("899.97");
        assertThat(reloaded.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    void savePreservesStatusTransitions() {
        Order domain = new Order("han@falcon.org", "HYPERDRIVE", 1, new BigDecimal("5000.00"));
        Order saved = adapter.save(domain);
        saved.confirm();
        adapter.save(saved);

        assertThat(adapter.findById(saved.getId()).get().getStatus())
                .isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        assertThat(adapter.findById("nope")).isEmpty();
    }
}
