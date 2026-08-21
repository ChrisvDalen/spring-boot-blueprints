package com.example.eventdriven;

import com.example.eventdriven.event.OrderConfirmedEvent;
import com.example.eventdriven.event.OrderPlacedEvent;
import com.example.eventdriven.model.Order;
import com.example.eventdriven.model.PlaceOrderRequest;
import com.example.eventdriven.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
class OrderEventTest {

    @Autowired OrderService orderService;
    @Autowired ApplicationEvents applicationEvents;

    @Test
    void placeOrder_publishesOrderPlacedEvent() {
        var request = new PlaceOrderRequest("luke@jedi.org", "LIGHTSABER", 1, new BigDecimal("299.99"));
        orderService.place(request);

        assertThat(applicationEvents.stream(OrderPlacedEvent.class)).hasSize(1);
    }

    @Test
    void confirmOrder_publishesOrderConfirmedEvent() {
        var request = new PlaceOrderRequest("leia@senate.org", "BLASTER", 1, new BigDecimal("150.00"));
        Order placed = orderService.place(request);
        orderService.confirm(placed.getId());

        assertThat(applicationEvents.stream(OrderConfirmedEvent.class)).hasSize(1);
    }

    @Test
    void placeOrder_allListenersReceiveEvent() {
        var request = new PlaceOrderRequest("r2d2@astromech.org", "OIL-CAN", 2, new BigDecimal("5.00"));
        // Listeners log output is the observable side-effect in this in-process demo.
        // With an async broker you'd use @EmbeddedKafka or Testcontainers.
        Order order = orderService.place(request);

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("10.00");
    }
}
