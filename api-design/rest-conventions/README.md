# REST Conventions Blueprint

## The WHY

REST is not a protocol — it is an architectural style. It has constraints.
One of those constraints is the **uniform interface**: resources are nouns,
HTTP methods are verbs, and status codes are the language of outcomes.

When developers ignore this, APIs become inconsistent dialects that every
consumer must separately learn. The Galactic Senate does not tolerate this.

## What This Module Demonstrates

### ✅ The Jedi Path

| Convention | Example | Why |
|-----------|---------|-----|
| Plural noun resources | `GET /articles` | Collection vs singleton distinction |
| HTTP verb carries the action | `DELETE /articles/1` (not `/deleteArticle?id=1`) | Standard semantics understood by every HTTP client |
| 201 + Location on creation | `POST /articles` → `201 Location: /articles/7` | Client knows where to find the new resource without parsing the body |
| 204 on deletion | `DELETE /articles/1` → `204 No Content` | No body needed — the status code is the answer |
| 404 when not found | `GET /articles/999` → `404` | Clients can detect missing resources without parsing a JSON envelope |
| PATCH for partial update | `PATCH /articles/1 {title: "..."}` | Only changes what was sent; idempotent; PUT replaces entirely |

### ❌ The Dark Side (Anti-Patterns)

See [`AntiPatternArticleController.java`](src/main/java/com/example/restconventions/controller/AntiPatternArticleController.java)

| Anti-Pattern | Problem |
|------------|---------|
| `GET /getArticle?id=1` | Verb in URL makes this RPC, not REST |
| `POST /updateArticle` | POST for everything loses idempotency and intent |
| `200 {"error": "not found"}` | HTTP status codes become meaningless; error detection requires JSON parsing |
| `GET /article` (singular) | Ambiguous — is this one article or the collection? |

## Key Design Decisions

**Why PATCH over PUT for updates?**
PATCH means "apply these changes". PUT means "replace everything with this".
With PUT, a client that only knows about `title` would accidentally clear `body` and
`authorId` by omitting them. PATCH only changes what you send.

**Why 201 + Location header?**
After creating a resource, the client needs to know where it lives. Putting the
URL in `Location` is the HTTP standard — it means clients don't need to know your
URL structure, and the server controls the canonical resource identifier.

**Why records for DTOs?**
Records are immutable by default, concise, and give you `equals/hashCode/toString`
for free. A DTO that can be mutated after construction is a source of subtle bugs.

## Running the Example

```bash
./mvnw spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- API docs JSON: http://localhost:8080/api-docs
- Try the anti-pattern endpoints: `GET /anti-pattern/articles/getArticle?id=1`

## Running Tests

```bash
./mvnw test
```
