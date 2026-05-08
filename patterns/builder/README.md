# Builder Pattern Blueprint

## The WHY

Some objects have many fields — most optional, some interdependent, some with
sensible defaults. Constructors don't scale here:

```java
// ANTI-PATTERN: telescoping constructor
new ReportSpec("sales", from, to, null, null, true, false, 100, null)
// Which field is which? What does the 4th null mean?
```

Setters don't scale either — they allow construction of invalid objects:

```java
// ANTI-PATTERN: mutable construction — object is invalid mid-build
ReportSpec spec = new ReportSpec();
spec.setFrom(LocalDate.now());
// spec is in an invalid state here — it has a 'from' but no type
spec.setReportType("sales");
```

The Builder pattern solves both:
- Every field has a name at the call site (`from(...)`, `to(...)`, `maxRows(...)`)
- The object is only created by calling `.build()` — which can validate invariants
- The resulting object is immutable

## Validation in `build()`

```java
public ReportSpec build() {
    if (from != null && to != null && from.isAfter(to)) {
        throw new IllegalStateException("'from' must not be after 'to'");
    }
    if (maxRows < 1 || maxRows > 100_000) {
        throw new IllegalStateException("maxRows must be between 1 and 100,000");
    }
    return new ReportSpec(this);
}
```

Cross-field invariants that `@NotNull` and `@Min` can't express are validated here,
before the object exists. An invalid `ReportSpec` is impossible to obtain.

## Package-Private Constructor

`ReportSpec`'s constructor is package-private — only `ReportSpec.Builder` can call it.
This enforces that every `ReportSpec` was validated through the builder.

## Why Not Lombok `@Builder`?

Lombok generates a builder, but:
- It doesn't let you add validation logic to `build()`
- It generates a public constructor alongside the builder
- It hides the implementation — students reading the code can't see how it works

For a learning repo, the explicit builder teaches the pattern. Use Lombok in
production if you prefer brevity and don't need `build()` validation.

## Running the Example

```bash
./mvnw spring-boot:run
# GET  /reports/spec/example  — see a hardcoded builder call in action
# POST /reports/spec          — build a spec from a JSON request body
```
