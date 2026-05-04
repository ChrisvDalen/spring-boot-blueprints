package com.example.eventdriven.event;

import java.time.Instant;

public record OrderConfirmedEvent(
        String orderId,
        String customerEmail,
        Instant occurredAt
) {}
