package com.example.eventdriven.event;

import java.time.Instant;

public record OrderShippedEvent(
        String orderId,
        String customerEmail,
        String trackingNumber,
        Instant occurredAt
) {}
