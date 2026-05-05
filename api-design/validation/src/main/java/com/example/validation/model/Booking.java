package com.example.validation.model;

import java.time.Instant;
import java.util.UUID;

public record Booking(
        String id,
        String guestName,
        String roomId,
        String startDate,
        String endDate,
        int guestCount,
        Instant createdAt
) {
    public static Booking from(BookingRequest request) {
        return new Booking(
                UUID.randomUUID().toString(),
                request.guestName(),
                request.roomId(),
                request.startDate(),
                request.endDate(),
                request.guestCount(),
                Instant.now()
        );
    }
}
