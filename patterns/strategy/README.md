# Strategy Pattern Blueprint

## The WHY

Shipping cost calculations differ by carrier. Payment processing differs by provider.
Export formatting differs by format. Discount logic differs by customer tier.

In each of these cases, the algorithm varies — but the caller doesn't care which
algorithm runs. It just wants a result.

Without the Strategy pattern:

```java
// ANTI-PATTERN: algorithm selection mixed with business logic
public ShippingQuote quote(String carrier, ShippingRequest request) {
    if ("standard".equals(carrier)) {
        return new ShippingQuote("Standard", new BigDecimal("5.00").add(...), ...);
    } else if ("express".equals(carrier)) {
        return new ShippingQuote("Express", new BigDecimal("15.00").add(...), ...);
    } else if ("hyperspace".equals(carrier)) {  // ← every new carrier adds here
        ...
    }
    throw new IllegalArgumentException("Unknown carrier: " + carrier);
}
```

Every new carrier modifies this method. Every modification risks breaking existing carriers.
The method grows without bound. Tests require increasingly complex branching.

## The Spring Twist

Spring makes the Strategy pattern nearly free with list injection:

```java
// Spring collects ALL ShippingStrategy beans into this list automatically
public ShippingService(List<ShippingStrategy> strategies) {
    this.strategies = strategies.stream()
        .collect(Collectors.toMap(ShippingStrategy::carrierId, Function.identity()));
}
```

Adding a new carrier:
1. Create a class that implements `ShippingStrategy`
2. Annotate it `@Component`
3. Done — Spring injects it automatically

`ShippingService` never changes. This is the Open/Closed Principle in action.

## Running the Example

```bash
./mvnw spring-boot:run
# GET  /shipping/carriers         — lists all registered carriers
# POST /shipping/quote/{carrier}  — quote from one carrier
# POST /shipping/quotes           — all carriers sorted by cost
```

```bash
curl -X POST http://localhost:8080/shipping/quotes \
  -H "Content-Type: application/json" \
  -d '{"origin":"Tatooine","destination":"Coruscant","weightKg":5.0}'
```
