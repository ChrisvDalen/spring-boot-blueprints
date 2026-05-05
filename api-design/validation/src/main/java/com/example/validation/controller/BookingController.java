package com.example.validation.controller;

import com.example.validation.model.Booking;
import com.example.validation.model.BookingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "Demonstrates Bean Validation and custom cross-field constraints")
public class BookingController {

    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @PostMapping
    @Operation(
            summary = "Create a booking",
            description = """
                    Validates:
                    - guestName: required, max 100 chars
                    - roomId: required
                    - startDate/endDate: required, ISO 8601 format, start before end (cross-field)
                    - guestCount: 1-10
                    """
    )
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = Booking.from(request);
        bookings.put(booking.id(), booking);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(booking.id()).toUri();
        return ResponseEntity.created(location).body(booking);
    }

    @GetMapping
    @Operation(summary = "List all bookings")
    public List<Booking> listBookings() {
        return new ArrayList<>(bookings.values());
    }
}
