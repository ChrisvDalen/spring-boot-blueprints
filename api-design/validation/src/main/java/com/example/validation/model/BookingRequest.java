package com.example.validation.model;

import com.example.validation.validator.DateRangeHolder;
import com.example.validation.validator.IsoDateRange;
import jakarta.validation.constraints.*;

/**
 * Demonstrates several validation techniques on a single record:
 * - Standard Bean Validation annotations
 * - Cross-field validation via a custom constraint on the type
 * - Pattern matching for format enforcement
 */
@IsoDateRange
public record BookingRequest(
        @NotBlank(message = "guestName is required")
        @Size(max = 100)
        String guestName,

        @NotBlank(message = "roomId is required")
        String roomId,

        @NotNull(message = "startDate is required")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate must be ISO 8601 (YYYY-MM-DD)")
        String startDate,

        @NotNull(message = "endDate is required")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate must be ISO 8601 (YYYY-MM-DD)")
        String endDate,

        @Min(value = 1, message = "guestCount must be at least 1")
        @Max(value = 10, message = "guestCount cannot exceed 10")
        int guestCount
) implements DateRangeHolder {}
