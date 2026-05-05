# 📡 The Galactic Senate API Design Standards

> *"Order 66 was a tragedy. Inconsistent API responses are worse."*
> — A Very Tired Backend Engineer

## Why This Module Exists

API design is where backend systems make their first promise to the world.
A well-designed API is a contract — and broken contracts lead to bugs that
are someone else's fault but your oncall page.

This module covers four pillars of production-quality API design:

| Sub-module | The Question It Answers |
|-----------|------------------------|
| [`rest-conventions/`](rest-conventions/) | What should my endpoints, verbs, and status codes look like? |
| [`error-handling/`](error-handling/) | What should my clients receive when things go wrong? |
| [`validation/`](validation/) | How do I validate input without writing a wall of if-statements? |
| [`openapi/`](openapi/) | How do I document my API in a way that generates client code? |

## Design Philosophy

Each sub-module pairs a **correct approach** with an **anti-pattern** so you
can see exactly what problem the pattern solves. The README for each explains
the WHY before the HOW.

## Running Any Sub-module

```bash
cd api-design/<sub-module>
./mvnw spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
```
