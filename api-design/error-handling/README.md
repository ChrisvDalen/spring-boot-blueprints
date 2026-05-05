# Error Handling Blueprint

## The WHY

Every API will fail. The question is not *whether* your clients will receive errors,
but *whether they can understand and handle them*.

A consistent, standard error response format is a first-class API contract.
When it's missing, every team that integrates with you writes bespoke error-parsing
code. When you change your error shape, you silently break them all.

**RFC 9457 Problem Details** is the IETF standard for HTTP error responses.
Spring Boot 3 supports it natively. Use it.

## What RFC 9457 Looks Like

```json
{
  "type": "https://example.com/problems/insufficient-stock",
  "title": "Insufficient Stock",
  "status": 409,
  "detail": "Insufficient stock for product: lightsaber-001",
  "productId": "lightsaber-001",
  "requested": 999,
  "available": 10
}
```

- `type`: A stable URI identifying the error category (can link to documentation)
- `title`: Human-readable summary — same for all instances of this error type
- `status`: The HTTP status code (repeated here for body-only consumers)
- `detail`: Human-readable explanation specific to this occurrence
- Custom properties: add domain-specific context (product ID, field names, etc.)

## Key Design Decisions

**Why `@RestControllerAdvice` instead of try/catch in controllers?**
Error handling is cross-cutting. One central handler means one place to change
the error format, one place to add logging, one place to apply security scrubbing.
Controllers stay focused on the happy path — they just throw.

**Why not catch `Exception` at the top level?**
Catching `Exception` hides bugs. Map only the exceptions you understand.
Let unexpected exceptions bubble to Spring's default handler (which returns 500).
This way, a 500 always means "something unexpected happened" — not "we swallowed
an error we should have handled."

**Why `spring.mvc.problemdetails.enabled=true`?**
This enables Spring Boot's built-in Problem Details for standard exceptions like
`MethodArgumentNotValidException`. You get standard error responses from Spring
itself without writing any handler code.

**Why custom `type` URIs?**
The `type` URI is meant to be a stable link. You can host a human-readable
explanation of each error type at that URL. Even if you don't, using consistent
URIs means clients can distinguish error categories in a forwards-compatible way.

## Anti-Patterns Shown

See [`AntiPatternOrderController.java`](src/main/java/com/example/errorhandling/controller/AntiPatternOrderController.java)

| Anti-Pattern | Problem |
|------------|---------|
| `{"success": false, "errorCode": "..."}` | Reinventing the wheel; no standard tooling support |
| `200 OK` with error body | HTTP status codes become lies; error detection requires full JSON parse |
| Swallowed exception → null response | Caller has no idea what happened; impossible to debug |
| Stack trace in response | Leaks implementation details; security risk; useless to API consumers |

## Running the Example

```bash
./mvnw spring-boot:run
```

Test the error scenarios:
```bash
# 409 Insufficient Stock
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":"lightsaber-001","quantity":999}'

# 404 Not Found with Problem Details
curl http://localhost:8080/orders/does-not-exist

# 422 Validation Error with field list
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":"","quantity":0}'
```
