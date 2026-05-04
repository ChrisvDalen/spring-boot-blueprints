# Validation Blueprint

## The WHY

Input validation is your first line of defence. Done badly, it becomes a wall
of nested `if` statements scattered across service and controller methods —
unmaintainable, inconsistent, untestable.

Bean Validation (Jakarta Validation) gives you declarative, annotation-driven
validation that lives where it belongs: on the data itself.

## What This Module Demonstrates

### Standard Constraint Annotations

```java
@NotBlank(message = "guestName is required")
@Size(max = 100)
String guestName,

@Min(1) @Max(10)
int guestCount
```

### Custom Cross-Field Constraint

Sometimes a field is only invalid in relation to another field.
`@NotNull` on both `startDate` and `endDate` won't catch `start > end`.

This module shows how to write a class-level constraint that can inspect
multiple fields at once:

```java
@IsoDateRange   // validates startDate < endDate across the whole record
public record BookingRequest(...) implements DateRangeHolder {}
```

The validator implements `ConstraintValidator<IsoDateRange, DateRangeHolder>`.
The `DateRangeHolder` interface decouples the validator from `BookingRequest`
specifically — the same validator works on any DTO that implements the interface.

### Why Records for Request DTOs?

Records are immutable after construction. A validated `BookingRequest` cannot
be silently mutated by any downstream code after the `@Valid` check runs.
With a mutable class, validation at the controller boundary gives no guarantee
about the state when the service receives the object.

## Anti-Pattern: Validation in the Service Layer

```java
// ANTI-PATTERN: manual validation buried in service code
public Booking book(BookingRequest request) {
    if (request.guestName() == null || request.guestName().isBlank()) {
        throw new IllegalArgumentException("guestName required");
    }
    if (request.guestCount() < 1 || request.guestCount() > 10) {
        throw new IllegalArgumentException("guestCount must be 1-10");
    }
    // ... business logic
}
```

Problems:
- Validation logic is duplicated if the service is called from multiple places
- Error messages are scattered; inconsistent format
- The service method is harder to read — validation noise drowns business logic
- Not testable via the Bean Validation API; must test through the service

## Running the Example

```bash
./mvnw spring-boot:run
```

```bash
# Valid booking
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{"guestName":"Luke Skywalker","roomId":"tatooine-suite","startDate":"2025-06-01","endDate":"2025-06-07","guestCount":2}'

# Cross-field failure: start after end
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{"guestName":"Han Solo","roomId":"falcon","startDate":"2025-06-10","endDate":"2025-06-01","guestCount":1}'
```
