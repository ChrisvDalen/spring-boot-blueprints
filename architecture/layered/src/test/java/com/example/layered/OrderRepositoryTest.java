package com.example.layered;

import com.example.layered.model.Order;
import com.example.layered.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence tests for the derived query methods — verifies the SQL that
 * Spring Data generates from the method names, isolated from the web layer.
 */
@DataJpaTest
class OrderRepositoryTest {

    @Autowired OrderRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(new Order("luke@jedi.org", "LIGHTSABER", 2, new BigDecimal("599.98")));
        repository.save(new Order("han@falcon.org", "HYPERDRIVE", 1, new BigDecimal("5000.00")));
    }

    @Test
    void findByCustomerEmail_returnsOnlyMatchingOrders() {
        var orders = repository.findByCustomerEmail("luke@jedi.org");
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getProductSku()).isEqualTo("LIGHTSABER");
    }

    @Test
    void findByCustomerEmail_unknownEmail_returnsEmpty() {
        assertThat(repository.findByCustomerEmail("nobody@nowhere.org")).isEmpty();
    }

    @Test
    void findByStatus_returnsAllPendingByDefault() {
        assertThat(repository.findByStatus(Order.OrderStatus.PENDING)).hasSize(2);
        assertThat(repository.findByStatus(Order.OrderStatus.CONFIRMED)).isEmpty();
    }

    @Test
    void findByStatus_reflectsStateChange() {
        var order = repository.findByCustomerEmail("han@falcon.org").getFirst();
        order.confirm();
        repository.save(order);

        assertThat(repository.findByStatus(Order.OrderStatus.CONFIRMED)).hasSize(1);
        assertThat(repository.findByStatus(Order.OrderStatus.PENDING)).hasSize(1);
    }
}
