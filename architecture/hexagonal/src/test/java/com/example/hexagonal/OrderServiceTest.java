package com.example.hexagonal;

import com.example.hexagonal.application.OrderService;
import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.port.out.OrderRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;

/**
 * Pure domain test — no Spring context, no database, no HTTP.
 * The application service is tested against a stub repository.
 * This is one of the core benefits of hexagonal architecture.
 */
class OrderServiceTest {

    private final OrderRepository stubRepository = new StubOrderRepository();
    private final OrderService orderService = new OrderService(stubRepository);

    @Test
    void place_calculatesTotal() {
        Order order = orderService.place("luke@jedi.org", "LIGHTSABER", 3, new BigDecimal("100.00"));

        assertThat(order.getTotalPrice()).isEqualByComparingTo("300.00");
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    void confirm_transitionsToPending() {
        Order placed = orderService.place("han@falcon.org", "HYPERDRIVE", 1, new BigDecimal("5000.00"));
        Order confirmed = orderService.confirm(placed.getId());

        assertThat(confirmed.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test
    void confirm_alreadyConfirmed_throws() {
        Order placed = orderService.place("leia@senate.org", "BLASTER", 1, new BigDecimal("200.00"));
        orderService.confirm(placed.getId());

        assertThatThrownBy(() -> orderService.confirm(placed.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING");
    }

    @Test
    void cancel_pendingOrder_succeeds() {
        Order placed = orderService.place("r2@astromech.org", "OIL-CAN", 1, new BigDecimal("10.00"));
        Order cancelled = orderService.cancel(placed.getId());

        assertThat(cancelled.getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        assertThat(orderService.findById("does-not-exist")).isEmpty();
    }

    // --- Stub (not a mock — stub returns canned data; no verify needed) ---
    static class StubOrderRepository implements OrderRepository {
        private final Map<String, Order> store = new ConcurrentHashMap<>();

        @Override
        public Order save(Order order) {
            store.put(order.getId(), order);
            return order;
        }

        @Override
        public Optional<Order> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Order> findAll() {
            return new ArrayList<>(store.values());
        }
    }
}
