# Hexagonal Architecture Blueprint

## The WHY

Layered architecture has a subtle problem: frameworks bleed into domain code.
JPA annotations appear on domain entities. Spring annotations appear in business
logic. The domain depends on infrastructure — and that makes it hard to test,
hard to move, and hard to reason about in isolation.

**Hexagonal architecture** (Ports and Adapters, by Alistair Cockburn) inverts this.
The domain defines what it *needs* (outbound ports) and what it *offers* (inbound
ports). Adapters implement those contracts. The domain depends on nothing external.

```
                    ┌──────────────────────────────┐
   HTTP Request ──► │  Web Adapter (inbound)        │
                    │  implements: nothing            │
                    │  depends on: inbound ports     │
                    └────────────┬─────────────────-─┘
                                 │
                    ┌────────────▼──────────────────┐
                    │         DOMAIN                 │
                    │  ┌──────────────────────────┐  │
                    │  │   Inbound Ports (use cases)│ │  ← what you can ask of the domain
                    │  ├──────────────────────────┤  │
                    │  │   Application Service     │  │  ← implements inbound, calls outbound
                    │  ├──────────────────────────┤  │
                    │  │   Outbound Ports          │  │  ← what the domain needs from outside
                    │  └──────────┬───────────────┘  │
                    └────────────┼───────────────────┘
                                 │
                    ┌────────────▼──────────────────┐
                    │  Persistence Adapter (outbound) │
                    │  implements: outbound ports     │
                    │  depends on: JPA, H2            │
                    └──────────────────────────────--─┘
```

## Key Structures in This Example

### Domain Port — Inbound (`PlaceOrderUseCase`, `ManageOrderUseCase`)
The domain defines what callers can ask of it. The web adapter depends on these
interfaces — not on `OrderService`. Swap the web adapter for a CLI adapter:
zero domain changes.

### Domain Port — Outbound (`OrderRepository` in `domain.port.out`)
The domain defines what it needs from storage. It knows nothing about JPA.
The persistence adapter implements this interface. Swap H2 for PostgreSQL
(or MongoDB): zero domain changes.

### Application Service (`OrderService`)
Implements inbound ports. Calls outbound ports. Orchestrates domain objects.
Has no framework annotations beyond `@Service`.

### Web Adapter (`OrderController`)
Inbound adapter. Translates HTTP into use-case calls. Depends on inbound port
interfaces. The controller doesn't need to change if you swap the application service.

### Persistence Adapter (`OrderPersistenceAdapter`)
Outbound adapter. Implements `OrderRepository` (the domain port) using JPA.
Contains all JPA-specific knowledge: `OrderJpaEntity`, mapping, queries.
The domain entity `Order` has no `@Entity` annotation — it's pure Java.

## The Critical Insight

```
domain.port.out.OrderRepository  ← defined by the domain
adapter.out.persistence.*        ← implements it with JPA
```

The arrow of dependency points **inward** — toward the domain.
In layered architecture, the domain depends on persistence. Here, it's the reverse.

## When to Choose Hexagonal

- Complex domain logic that needs to be tested without a database
- Multiple delivery mechanisms (HTTP, CLI, gRPC, messaging)
- Long-lived systems where infrastructure choices may change
- Teams that practise domain-driven design

## Running the Example

```bash
./mvnw spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
```

## Running Tests

```bash
./mvnw test
# OrderServiceTest runs with no Spring context — pure domain test
```
