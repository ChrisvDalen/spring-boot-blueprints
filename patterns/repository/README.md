# Repository Pattern Blueprint

## The WHY

Without the repository pattern, data access logic leaks everywhere.
Service methods write JPQL directly. Controllers call `EntityManager`.
Business logic sits next to SQL strings. Switching databases requires
surgery on every class that ever touched data.

The repository pattern creates a clean boundary: the domain describes what
data operations it needs (via an interface), and infrastructure provides
them (via an implementation). The domain never knows whether it's talking
to H2, PostgreSQL, MongoDB, or a HashMap.

## Two Implementations, One Interface

This module ships two implementations of `ProductRepository`:

| Implementation | Location | Used for |
|---------------|----------|---------|
| `JpaProductRepository` | `infrastructure/jpa/` | Production — maps to H2/PostgreSQL via JPA |
| `InMemoryProductRepository` | `infrastructure/inmemory/` | Tests — no database required |

The service (`ProductService`) depends only on `ProductRepository` (the interface).
It never knows which implementation is active.

## The Interface Speaks Domain Language

```java
// Good — domain language
List<Product> findByCategory(String category);
List<Product> findLowStock(int threshold);

// Bad — leaking infrastructure concerns into the domain
List<Product> findByStockLessThanEqualOrderByNameAsc(int stock);  // JPA naming convention leak
```

The interface methods use words from the business domain. The JPA implementation
translates those to SQL internally. Callers never see `@Query` or `JPQL`.

## The Anti-Pattern: Repository Bypassing

```java
// ANTI-PATTERN: service uses EntityManager directly
@Service
public class ProductService {
    @PersistenceContext
    private EntityManager em;  // now the service knows about JPA

    public List<Product> findLowStock(int threshold) {
        return em.createQuery(
            "SELECT p FROM ProductEntity p WHERE p.stock <= :t", ProductEntity.class)
            .setParameter("t", threshold)
            .getResultList();
    }
}
```

Problems: service is coupled to JPA, untestable without a database, JPQL
string is untyped, and switching to MongoDB requires rewriting the service.

## Running the Example

```bash
./mvnw spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console (jdbc:h2:mem:repopatterndb)
```

## Running Tests

```bash
./mvnw test
# ProductServiceTest runs with InMemoryProductRepository — no DB needed
```
