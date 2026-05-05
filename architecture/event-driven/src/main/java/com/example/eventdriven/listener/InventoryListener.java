package com.example.eventdriven.listener;

import com.example.eventdriven.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A second independent listener on the same event.
 *
 * OrderService doesn't know InventoryListener exists.
 * InventoryListener doesn't know NotificationListener exists.
 * They both react to the same event without coupling to each other or the publisher.
 */
@Component
public class InventoryListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryListener.class);

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        // In production: call inventory service to reserve stock
        log.info("[INVENTORY] Reserving {}x {} for order {}",
                event.quantity(), event.productSku(), event.orderId());
    }
}
