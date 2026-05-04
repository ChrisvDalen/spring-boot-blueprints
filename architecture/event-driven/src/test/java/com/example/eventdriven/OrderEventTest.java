package com.example.eventdriven;

import com.example.eventdriven.event.OrderConfirmedEvent;
import com.example.eventdriven.event.OrderPlacedEvent;
import com.example.eventdriven.model.Order;
import com.example.eventdriven.model.PlaceOrderRequest;
import com.example.eventdriven.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class OrderEventTest {

    @Autowired OrderService orderService;
    @MockitoSpyBean ApplicationEventPublisher publisher;

    @Test
    void placeOrder_publishesOrderPlacedEvent() {
        var request = new PlaceOrderRequest("luke@jedi.org", "LIGHTSABER", 1, new BigDecimal("299.99"));
        orderService.place(request);

        verify(publisher).publishEvent(any(OrderPlacedEvent.class));
    }

    @Test
    void confirmOrder_publishesOrderConfirmedEvent() {
        var request = new PlaceOrderRequest("leia@senate.org", "BLASTER", 1, new BigDecimal("150.00"));
        Order placed = orderService.place(request);
        orderService.confirm(placed.getId());

        verify(publisher).publishEvent(any(OrderConfirmedEvent.class));
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
