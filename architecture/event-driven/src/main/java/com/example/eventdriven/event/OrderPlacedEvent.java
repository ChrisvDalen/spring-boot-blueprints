package com.example.eventdriven.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain event: something that happened in the domain.
 * Named in the past tense — it already occurred.
 *
 * Events are immutable records: they describe a fact, not a command.
 * Listeners react to this fact independently of each other and independently
 * of the service that published it.
 */
public record OrderPlacedEvent(
        String orderId,
        String customerEmail,
        String productSku,
        int quantity,
        BigDecimal totalPrice,
        Instant occurredAt
) {}
