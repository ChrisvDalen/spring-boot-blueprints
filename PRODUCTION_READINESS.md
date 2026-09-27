# Production Readiness Assessment

Repository: `spring-boot-blueprints` — a monorepo of 7 self-contained Spring Boot blueprint
modules (4 under `api-design/`, 3 under `architecture/`). Each module is an independent
Maven project with its own `pom.xml`, README, and demo application.

- Java: 25 (Temurin LTS)
- Spring Boot: 3.5.0 → upgraded to 3.5.16 (see LCM section)
- Build: Maven (3.9.x compatible; POMs previously required Maven 4 RC)
- Persistence: in-memory H2 (runtime only, 3 modules); the other 4 modules are stateless demos
- CI: none present → added GitHub Actions workflow
- Frontend: none (README references future sectors that do not exist yet)

---

## Phase 1 — Initial findings (baseline, before changes)

### CRITICAL

| # | Problem | Impact | Proposed solution | Risk | Fixed? |
|---|---------|--------|-------------------|------|--------|
| C1 | **2 of 7 module builds fail on `main`** — `error-handling` test expects 422 but receives 400; `event-driven` context fails to start in tests (`@MockitoSpyBean ApplicationEventPublisher` — no such bean exists). | The repository's central promise is "executable truth": `./mvnw verify` must pass in every module. Two failing modules break CI, onboarding, and trust in the blueprints. | Root-cause fixes: (1) give the application's `@RestControllerAdvice` an explicit `@Order(Ordered.HIGHEST_PRECEDENCE)` so it out-prioritises the framework's `ProblemDetailsExceptionHandler` (order 0), restoring the 422 + field-errors contract; (2) replace the broken spy with a test-only recording listener that asserts real event delivery. | Low — both fixes preserve the documented behaviour and make tests match the code's intent. | ✅ |
| C2 | **All 7 POMs declare `modelVersion 4.1.0`**, which only the Maven 4 RCs accept. Maven 3.9.x (the current stable) refuses to even parse the POMs. | The modules cannot be built at all with any released, stable Maven. | Change `modelVersion` to `4.0.0` (accepted by both Maven 3 and Maven 4). | None — additive compatibility. | ✅ |

### HIGH

| # | Problem | Impact | Proposed solution | Risk | Fixed? |
|---|---------|--------|-------------------|------|--------|
| H1 | **No Maven wrapper (`mvnw`) in any module**, although every README says to run `./mvnw spring-boot:run`. | First-run developer experience is broken; builds are not reproducible. | Generate `mvnw`/`mvnw.cmd`/`.mvn/wrapper` in each module, pinned to Maven 3.9.x stable (Maven 4 is still RC). | Low. | ✅ |
| H2 | **Spring Boot 3.5.0 is an old patch release** (3.5.16 is current at time of writing); springdoc 2.8.9 is also behind (2.9.1). | Security patches and bug fixes of the underlying platform are missing; CVEs fixed in patch releases remain open. | Upgrade all modules to Boot 3.5.16 and springdoc 2.9.1 (same minor line — API-compatible). | Low — patch/minor upgrade, validated by full test runs. | ✅ |
| H3 | **H2 web console enabled unconditionally** in the 3 JPA modules (`spring.h2.console.enabled=true`). | In a production-style deployment the console is an unauthenticated data-access endpoint (Spring Security is not on the classpath, so the console would be fully open on any deployment that ships this `application.yml`). | Move the console to a `dev` profile only, documented in READMEs. | Low — demo apps still run with `--spring.profiles.active=dev`. | ✅ |
| H4 | **No CI workflow** — no automated build/test gate despite the README badge referencing a non-existent `ci.yml`. | Regressions land on `main` silently (as C1 demonstrates). | Add `.github/workflows/ci.yml`: sequential build of all 7 modules with dependency caching, test reports, and OWASP dependency check. | Low. | ✅ |
| H5 | **No `LICENSE` file** although the README links to one and declares MIT. | Legal/licensing ambiguity for an OSS blueprint repo. | Add standard MIT `LICENSE`. | None. | ✅ |
| H6 | **`hexagonal` module returns raw `IllegalArgumentException` as 500** when a controller calls `confirm`/`cancel` with an unknown order id (the service throws `IllegalArgumentException("Order not found")`). | API contract break: a missing resource must be a 404, not a 500; clients cannot rely on the error shape. | Add a `GlobalExceptionHandler` in the hexagonal web adapter mapping the domain "not found" case to 404 Problem Details. | Low — additive, preserves existing behaviour for all other paths. | ✅ |

### MEDIUM

| # | Problem | Impact | Proposed solution | Risk | Fixed? |
|---|---------|--------|-------------------|------|--------|
| M1 | No graceful shutdown, no explicit timeout settings, no health checks. | Ungraceful request loss on restart; no readiness checks for containers. | Add `server.shutdown=graceful` + `spring.lifecycle.timeout-per-shutdown-phase` in each module's `application.yml`. | Low. | ✅ |
| M2 | Test coverage is thin: 22 tests total across 7 modules; no tests for `rest-conventions` PATCH, `openapi` pagination/404/400 paths, `validation` list endpoint, `layered`/`hexagonal`/`event-driven` controllers or repositories. | The most error-prone seams (HTTP mapping, pagination math, repository queries, state transitions) are unguarded. | Add meaningful MockMvc/controller tests, validator unit tests, and repository slice tests per module. | Low — tests only. | ✅ |
| M3 | No `.gitignore` — IDE files, `target/`, and logs would be committed by contributors. | Repository hygiene / accidental build-artifact commits. | Add a Java/Maven `.gitignore`. | None. | ✅ |
| M4 | `error-handling` module keeps its order store in a controller field and does all logic there. | The module's own README teaches "service layer owns business logic", yet the demo does not follow it. | Extract an `OrderService` (constructor-injected) mirroring the rest of the repo's layering; behaviour unchanged. | Low. | ✅ |
| M5 | `rest-conventions` `GlobalExceptionHandler` omits `type`/`title`, inconsistent with the RFC 9457 standard the sibling module demonstrates. | Inconsistent error contract across the repo's own blueprints. | Align with the `error-handling` style (type URI, title). | Low. | ✅ |
| M6 | `springdoc` version duplicated as a magic number in all 7 POMs; no single build entry point. | Drift risk between modules when upgrading; slow to verify the whole repo. | Keep a single `springdoc.version` property per module (already present); add a root aggregator POM so the whole repo builds with one `mvn verify`. | Low. | ✅ |

### LOW

| # | Problem | Impact | Proposed solution | Risk | Fixed? |
|---|---------|--------|-------------------|------|--------|
| L1 | README references sectors (`patterns/`, `modules/`, `testing/`, `observability/`, `fullstack/`) that do not exist in the repo. | Misleading navigation. | Mark the future sectors explicitly as "planned" in the README star map. | None. | ✅ |
| L2 | `openapi` module's pagination silently clamps out-of-range pages. | Boundary behaviour untested. | Add boundary tests (documented clamping is intentional — behaviour preserved). | None. | ✅ |
| L3 | `event-driven` `OrderJpaRepository` is a nested interface inside `OrderService`. | Slight package-boundary smell (repository in service package). | Accepted as-is for the demo: it keeps the module minimal; documented in the report. | — | ⏸ documented |
| L4 | All modules bind port 8080. | Cannot run several demos side by side without `--server.port`. | Add per-module default ports in `application.yml`. | Low. | ✅ |

**Baseline validation (before changes):**

```
$ mvn -B clean verify   (per module, JDK 25.0.4, Maven 3.9.16)
PASS: api-design/rest-conventions      (4 tests)
FAIL: api-design/error-handling        (1 of 4 tests: expected 422, got 400)
PASS: api-design/validation            (7 tests)
PASS: api-design/openapi               (5 tests)
FAIL: architecture/event-driven        (context startup failure — @MockitoSpyBean)
PASS: architecture/hexagonal           (5 tests)
PASS: architecture/layered             (5 tests)
```

---

*(The remaining sections — LCM, Testing, Security, Architecture, CI/CD, Validation,
Remaining risks, Recommended next actions — are finalised in the closing report.)*
