package com.example.eventdriven.listener;

import com.example.eventdriven.event.OrderConfirmedEvent;
import com.example.eventdriven.event.OrderPlacedEvent;
import com.example.eventdriven.event.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Audit listener — records every domain event for compliance.
 * In a real system this would write to an immutable audit log / event store.
 * Added with zero changes to any existing class.
 */
@Component
public class AuditListener {

    private static final Logger log = LoggerFactory.getLogger(AuditListener.class);

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("[AUDIT] OrderPlaced  | id={} customer={} total={}",
                event.orderId(), event.customerEmail(), event.totalPrice());
    }

    @EventListener
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("[AUDIT] OrderConfirmed | id={} customer={}",
                event.orderId(), event.customerEmail());
    }

    @EventListener
    public void onOrderShipped(OrderShippedEvent event) {
        log.info("[AUDIT] OrderShipped   | id={} tracking={}",
                event.orderId(), event.trackingNumber());
    }
}
