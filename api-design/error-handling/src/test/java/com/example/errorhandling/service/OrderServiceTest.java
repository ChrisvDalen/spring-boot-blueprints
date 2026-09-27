package com.example.errorhandling.service;

import com.example.errorhandling.exception.InsufficientStockException;
import com.example.errorhandling.exception.OrderNotFoundException;
import com.example.errorhandling.model.Order;
import com.example.errorhandling.model.PlaceOrderRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit test for the stock-limit rule and price computation — no Spring, no HTTP.
 */
class OrderServiceTest {

    private final OrderService service = new OrderService();

    @Test
    void place_atStockLimit_succeeds() {
        Order order = service.place(new PlaceOrderRequest("pad-1", OrderService.STOCK_LIMIT));
        assertThat(order.quantity()).isEqualTo(OrderService.STOCK_LIMIT);
    }

    @Test
    void place_aboveStockLimit_throwsInsufficientStock() {
        assertThatThrownBy(() -> service.place(new PlaceOrderRequest("pad-1", OrderService.STOCK_LIMIT + 1)))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void place_computesTotalWithoutFloatingPointDrift() {
        Order order = service.place(new PlaceOrderRequest("kyber-crystal", 3));
        // 3 x 9.99 must be exactly 29.97 — the old float arithmetic produced 29.970000000000002
        assertThat(order.total()).isEqualByComparingTo(new BigDecimal("29.97"));
    }

    @Test
    void get_unknownId_throwsOrderNotFound() {
        assertThatThrownBy(() -> service.get("nope"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void place_thenGet_returnsSameOrder() {
        Order order = service.place(new PlaceOrderRequest("holo-cronometer", 1));
        Order fetched = service.get(order.id());
        assertThat(fetched.productId()).isEqualTo("holo-cronometer");
        assertThat(fetched.total()).isEqualByComparingTo(new BigDecimal("9.99"));
    }
}
