# ⚔️ The Jedi Pattern Archives

> *"These ancient scrolls were written not to be memorised, but to be understood.
> A pattern applied without understanding is just cargo-cult programming."*
> — Master Obi-Wan Kenobi, Design Patterns Quarterly

## Why Design Patterns?

Design patterns are not rules — they are named solutions to recurring problems.
The name matters because it gives teams a shared vocabulary. Saying
"let's use a Strategy here" in a code review is faster than explaining
the entire concept from scratch every time.

The four patterns here are the most commonly useful in Spring Boot applications.
Each one shows the problem it solves alongside the solution.

| Pattern | Path | The Problem It Solves |
|---------|------|----------------------|
| Repository | [`repository/`](repository/) | Decouple domain logic from data access technology |
| Factory | [`factory/`](factory/) | Centralise complex object creation with conditional logic |
| Strategy | [`strategy/`](strategy/) | Swap algorithms at runtime without changing callers |
| Builder | [`builder/`](builder/) | Construct complex objects step-by-step without telescoping constructors |

## Running Any Sub-module

```bash
cd patterns/<sub-module>
./mvnw spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
```
