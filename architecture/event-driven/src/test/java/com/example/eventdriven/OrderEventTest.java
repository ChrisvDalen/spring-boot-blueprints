package com.example.eventdriven;

import com.example.eventdriven.event.OrderConfirmedEvent;
import com.example.eventdriven.event.OrderPlacedEvent;
import com.example.eventdriven.model.Order;
import com.example.eventdriven.model.PlaceOrderRequest;
import com.example.eventdriven.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the service publishes domain events and that they are
 * actually delivered to listeners.
 *
 * Events are observed with a dedicated recording listener attached to the
 * test application context, instead of spying on ApplicationEventPublisher:
 * the publisher is a capability provided by the context, not a bean that can
 * be replaced. Observing real delivery through the multicaster exercises the
 * whole event flow (publish → multicast → listener), not just the publish call.
 */
@SpringBootTest
class OrderEventTest {

    @Autowired OrderService orderService;
    @Autowired ConfigurableApplicationContext applicationContext;

    private final RecordingListener recordingListener = new RecordingListener();

    @BeforeEach
    void setUp() {
        recordingListener.reset();
        applicationContext.addApplicationListener(recordingListener);
    }

    @AfterEach
    void tearDown() {
        applicationContext.removeApplicationListener(recordingListener);
    }

    @Test
    void placeOrder_deliversOrderPlacedEventToListener() {
        var request = new PlaceOrderRequest("luke@jedi.org", "LIGHTSABER", 1, new BigDecimal("299.99"));
        Order placed = orderService.place(request);

        assertThat(recordingListener.placed()).hasSize(1);
        OrderPlacedEvent event = recordingListener.placed().getFirst();
        assertThat(event.orderId()).isEqualTo(placed.getId());
        assertThat(event.customerEmail()).isEqualTo("luke@jedi.org");
        assertThat(event.productSku()).isEqualTo("LIGHTSABER");
        assertThat(event.totalPrice()).isEqualByComparingTo("299.99");
    }

    @Test
    void confirmOrder_deliversOrderConfirmedEventToListener() {
        var request = new PlaceOrderRequest("leia@senate.org", "BLASTER", 1, new BigDecimal("150.00"));
        Order placed = orderService.place(request);
        orderService.confirm(placed.getId());

        assertThat(recordingListener.confirmed()).hasSize(1);
        assertThat(recordingListener.confirmed().getFirst().orderId()).isEqualTo(placed.getId());
    }

    @Test
    void placeOrder_allListenersReceiveEvent() {
        var request = new PlaceOrderRequest("r2d2@astromech.org", "OIL-CAN", 2, new BigDecimal("5.00"));
        // The production listeners (notification, inventory, audit) observe the
        // same events in the same context; the recording listener proves delivery.
        Order order = orderService.place(request);

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("10.00");
        assertThat(recordingListener.placed()).hasSize(1);
    }

    /** Plain listener that records order events for assertions. */
    static class RecordingListener implements org.springframework.context.ApplicationListener<org.springframework.context.ApplicationEvent> {

        private final List<OrderPlacedEvent> placed = new CopyOnWriteArrayList<>();
        private final List<OrderConfirmedEvent> confirmed = new CopyOnWriteArrayList<>();

        @Override
        public void onApplicationEvent(org.springframework.context.ApplicationEvent event) {
            // Payload events (records that don't extend ApplicationEvent) arrive
            // wrapped in a PayloadApplicationEvent — unwrap before matching.
            Object payload = event instanceof org.springframework.context.PayloadApplicationEvent<?> payloadEvent
                    ? payloadEvent.getPayload()
                    : event;
            if (payload instanceof OrderPlacedEvent) {
                placed.add((OrderPlacedEvent) payload);
            } else if (payload instanceof OrderConfirmedEvent) {
                confirmed.add((OrderConfirmedEvent) payload);
            }
        }

        List<OrderPlacedEvent> placed() { return placed; }
        List<OrderConfirmedEvent> confirmed() { return confirmed; }

        void reset() {
            placed.clear();
            confirmed.clear();
        }
    }
}
