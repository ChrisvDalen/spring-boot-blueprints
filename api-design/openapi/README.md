# OpenAPI Blueprint

## The WHY

An API without documentation is a black box. An API with stale documentation
is worse — it's a lie. The only documentation that can't go stale is the
documentation generated from the code that runs in production.

Springdoc OpenAPI generates a live, accurate OpenAPI 3 spec from your running
application. That spec powers Swagger UI, enables client code generation, and
serves as the single source of truth for your API contract.

## What This Module Demonstrates

### Centralised API Metadata

See [`OpenApiConfig.java`](src/main/java/com/example/openapi/config/OpenApiConfig.java).

A single `@Bean` configures the API title, description, version, contact info,
and security schemes. This information appears in the Swagger UI header and is
exported in the spec for downstream consumers.

### Security Scheme Declaration

```java
.components(new Components()
    .addSecuritySchemes("bearerAuth", new SecurityScheme()
        .type(HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")))
```

Declaring security schemes in the spec means API clients and documentation tools
know exactly how to authenticate. Without this, "Authorize" in Swagger UI doesn't work.

### `@Schema` on Records

```java
@Schema(description = "A product available in the Galactic Republic catalogue")
public record Product(
    @Schema(description = "Unique product identifier", example = "prod-abc-123")
    String id,
    ...
)
```

Examples in `@Schema` populate the "Try it out" form in Swagger UI with realistic
values, dramatically improving the development experience.

### Documenting All Response Codes

```java
@ApiResponse(responseCode = "201", description = "Product created",
    headers = @Header(name = "Location", description = "URI of the created product"))
@ApiResponse(responseCode = "400", description = "Invalid request body",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "401", description = "Unauthenticated")
```

Documenting error responses in the spec means client code generators produce
typed error-handling code, not just generic exception handling.

### Paginated Responses

The `PagedResponse<T>` generic record shows how to document a reusable pagination
envelope with typed content. The `@Schema` annotations on each field appear in
the generated spec as property descriptions.

## Anti-Pattern: No Annotations = Unusable Spec

Without `@Operation`, `@Parameter`, and `@ApiResponse`, Springdoc still generates
a spec — but it's a skeleton with no descriptions, no examples, and no documented
error codes. The result is technically valid but practically useless.

Document as you code. The annotations are cheap; the lost developer-hours from
an undocumented API are not.

## Running the Example

```bash
./mvnw spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
- OpenAPI YAML: http://localhost:8080/api-docs.yaml

The YAML file can be fed directly into code generators:
```bash
# Generate a TypeScript Angular client
openapi-generator-cli generate \
  -i http://localhost:8080/api-docs.yaml \
  -g typescript-angular \
  -o ./generated-client
```
