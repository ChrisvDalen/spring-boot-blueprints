# Event-Driven Architecture Blueprint

## The WHY

When `OrderService.place()` also needs to send a confirmation email, reserve
inventory, write an audit log, and award loyalty points — what do you do?

The naive answer: inject those services and call them all.

```java
// ANTI-PATTERN: direct coupling to every side effect
public Order place(request) {
    Order order = repo.save(...);
    emailService.sendConfirmation(order);      // now coupled to email
    inventoryService.reserve(order);           // now coupled to inventory
    auditService.log(order);                   // now coupled to audit
    loyaltyService.awardPoints(order);         // now coupled to loyalty
    return order;
}
```

Problems:
- `OrderService` needs to know about email, inventory, audit, and loyalty
- Adding a new side effect requires changing `OrderService`
- A failure in email sending rolls back the entire order transaction
- Testing `OrderService` requires mocking four other services

**Event-driven architecture solves this.**

## How It Works

The service publishes a domain event — a record of what happened. Listeners
react to the event independently. The service knows nothing about its listeners.

```
OrderService.place()
    │
    ├── saves order to DB
    └── publishes OrderPlacedEvent
              │
              ├──► NotificationListener  → sends confirmation email
              ├──► InventoryListener     → reserves stock
              └──► AuditListener        → writes audit record
```

Adding a `LoyaltyListener` requires **zero changes** to `OrderService`.

## Spring's Event System

Spring's `ApplicationEventPublisher` is synchronous and in-process by default.
This example uses it directly, which is suitable for:
- Side effects that must happen in the same transaction
- Simple monolith architectures that want logical decoupling without a broker

For **true decoupling** (side effects in separate services), replace the
in-process event with a Kafka or RabbitMQ publish. The service code stays
identical — only the listener implementation changes.

## Events Are Past Tense

```java
// Correct: describes what happened
record OrderPlacedEvent(String orderId, ...) {}

// Wrong: this is a command, not an event
record PlaceOrderCommand(String orderId, ...) {}
```

Events describe immutable facts. They are records (immutable by definition).
The name is past tense because the thing already happened.

## The Transactional Outbox Note

`ApplicationEventPublisher` publishes within the same transaction. If the
transaction commits but the listener fails, you have inconsistency. For
guaranteed delivery with an async broker, combine events with the
**Transactional Outbox Pattern** — write the event to a DB table in the same
transaction, then a separate process polls and forwards it to the broker.

## Listeners in This Example

| Listener | Reacts To | Simulated Action |
|---------|-----------|-----------------|
| `NotificationListener` | Placed, Confirmed, Shipped | Log email send |
| `InventoryListener` | Placed | Log stock reservation |
| `AuditListener` | All events | Log audit record |

## When to Choose Event-Driven

- Multiple independent side effects on the same domain action
- Audit trails — events are a natural log of what happened
- Eventual consistency requirements between services
- When you want to add behaviour without modifying existing classes

## Running the Example

```bash
./mvnw spring-boot:run
# Place an order and watch all three listeners log in the console
```

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail":"luke@jedi.org","productSku":"LIGHTSABER","quantity":1,"unitPrice":"299.99"}'
```

Expected console output:
```
[NOTIFICATION] Order confirmation email → luke@jedi.org for order ... (1x LIGHTSABER)
[INVENTORY]    Reserving 1x LIGHTSABER for order ...
[AUDIT]        OrderPlaced  | id=... customer=luke@jedi.org total=299.99
```
