# 🏛️ The Architecture Sector

> *"Your architects, they were. Poor judgement, they had."*
> — Master Yoda, reviewing the Death Star codebase

## Why Architecture Matters

Architecture is the set of decisions that are hard to change later.
Get them wrong and every feature becomes a struggle against the structure.
Get them right and the structure accelerates you.

This sector presents three architectures — not as religious doctrine, but as
tools with different tradeoffs. Know when to reach for each one.

| Sub-module | Architecture | When to Use |
|-----------|-------------|-------------|
| [`layered/`](layered/) | Layered (N-Tier) | Most CRUD apps; teams familiar with MVC; straightforward domains |
| [`hexagonal/`](hexagonal/) | Hexagonal (Ports & Adapters) | Complex domain logic; multiple delivery mechanisms; long-lived systems |
| [`event-driven/`](event-driven/) | Event-Driven | Decoupled services; audit trails; eventual consistency requirements |

## The Common Thread

All three examples use the same domain: **a simple order management system**.
Same problem, different structural solutions — so the tradeoffs are visible.

## Running Any Sub-module

```bash
cd architecture/<sub-module>
./mvnw spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
```
