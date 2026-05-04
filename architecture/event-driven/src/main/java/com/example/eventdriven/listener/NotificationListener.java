package com.example.eventdriven.listener;

import com.example.eventdriven.event.OrderConfirmedEvent;
import com.example.eventdriven.event.OrderPlacedEvent;
import com.example.eventdriven.event.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Notification listener — reacts to order events to send customer messages.
 *
 * This class knows nothing about how orders are placed, stored, or processed.
 * It only knows: "when this event happens, do this notification work."
 *
 * Adding this listener required ZERO changes to OrderService.
 * Removing it would also require ZERO changes to OrderService.
 * That's the power of event-driven decoupling.
 */
@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        // In production: send email via SendGrid, SES, etc.
        log.info("[NOTIFICATION] Order confirmation email → {} for order {} ({}x {})",
                event.customerEmail(), event.orderId(), event.quantity(), event.productSku());
    }

    @EventListener
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("[NOTIFICATION] Order confirmed email → {} for order {}",
                event.customerEmail(), event.orderId());
    }

    @EventListener
    public void onOrderShipped(OrderShippedEvent event) {
        log.info("[NOTIFICATION] Shipping notification → {} | order {} | tracking {}",
                event.customerEmail(), event.orderId(), event.trackingNumber());
    }
}
