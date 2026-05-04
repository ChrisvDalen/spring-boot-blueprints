# Layered Architecture Blueprint

## The WHY

Layered architecture is the most widely understood structure in enterprise
software. It divides code into horizontal layers — each with a single
responsibility — and enforces a strict dependency direction: upper layers
depend on lower layers, never the reverse.

```
┌─────────────────────────────────┐
│         Controller Layer         │  ← HTTP, JSON, request/response mapping
├─────────────────────────────────┤
│          Service Layer           │  ← Business logic, transaction boundaries
├─────────────────────────────────┤
│        Repository Layer          │  ← Data access, queries
├─────────────────────────────────┤
│           Domain Model           │  ← Entities, value objects, business rules
└─────────────────────────────────┘
```

**Why this structure?**
- Every developer knows where to look for a given type of code
- Each layer can be tested at its own boundary
- Changes to persistence (e.g. H2 → PostgreSQL) don't touch the controller
- Changes to the HTTP API don't touch the repository

## The Layers in This Example

### Controller (`OrderController`)
HTTP only. Translates requests into service calls and service results into
HTTP responses. No `if` statements for business logic — only routing.

### Service (`OrderService`)
Transaction boundaries live here. Business orchestration lives here.
The service loads domain objects, calls their methods, and saves them back.
It depends on the repository interface (not the JPA implementation).

### Repository (`OrderRepository`)
Spring Data JPA interface. The service knows this interface; it does not know
about H2, SQL, or entity caching.

### Domain Model (`Order`)
Business methods live on the entity: `confirm()`, `cancel()`. This keeps the
domain rule ("you can't cancel a shipped order") co-located with the data it
protects — not scattered across service methods.

## Anti-Pattern: Anemic Domain Model

```java
// ANTI-PATTERN: business logic in the service, entity is just a data bag
public OrderResponse confirm(Long id) {
    Order order = repo.findById(id)...;
    if (order.getStatus() != PENDING) {   // ← business rule in service
        throw new ...
    }
    order.setStatus(CONFIRMED);           // ← mutation via setter
    return repo.save(order);
}
```

vs the correct approach where the rule lives on the entity:

```java
order.confirm();  // entity validates its own invariants
```

## When to Choose Layered

- CRUD-heavy applications with straightforward business rules
- Small-to-medium teams that value convention over configuration
- Greenfield projects where the domain complexity is not yet clear

When domain logic becomes complex and the service layer balloons into
thousands of lines, consider migrating toward hexagonal architecture.

## Running the Example

```bash
./mvnw spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console (jdbc:h2:mem:layereddb)
```

## Running Tests

```bash
./mvnw test
```
