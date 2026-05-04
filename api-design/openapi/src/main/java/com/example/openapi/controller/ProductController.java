package com.example.openapi.controller;

import com.example.openapi.model.CreateProductRequest;
import com.example.openapi.model.PagedResponse;
import com.example.openapi.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates thorough OpenAPI annotation coverage:
 * - @Tag for grouping in Swagger UI
 * - @Operation with summary and description
 * - @Parameter for path/query parameters with examples
 * - @ApiResponse for every documented status code
 * - @Schema on models for property-level documentation
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Galactic Republic Product Catalogue")
public class ProductController {

    private final Map<String, Product> products = new ConcurrentHashMap<>();

    @GetMapping
    @Operation(
            summary = "List products",
            description = "Returns a paginated list of products. Filter by category using the `category` query parameter."
    )
    @ApiResponse(responseCode = "200", description = "Product list returned successfully")
    public PagedResponse<Product> listProducts(
            @Parameter(description = "Filter by category", example = "WEAPONS")
            @RequestParam(required = false) Product.Category category,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Items per page", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Product> filtered = products.values().stream()
                .filter(p -> category == null || p.category() == category)
                .toList();
        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        return PagedResponse.of(filtered.subList(from, to), page, size, filtered.size());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Product> getProduct(
            @Parameter(description = "Product ID", example = "prod-abc-123")
            @PathVariable String id
    ) {
        Product product = products.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @Operation(
            summary = "Create a product",
            description = "Adds a new product to the catalogue. Requires authentication."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Product created",
            headers = @Header(name = "Location", description = "URI of the created product")
    )
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = new Product(
                UUID.randomUUID().toString(),
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.stock(),
                Instant.now()
        );
        products.put(product.id(), product);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(product.id()).toUri();
        return ResponseEntity.created(location).body(product);
    }

    // Seed data so the Swagger UI has something to show
    @jakarta.annotation.PostConstruct
    void seed() {
        var lightsaber = new Product("prod-ls-001", "Lightsaber (Blue)", "Standard Jedi weapon",
                new BigDecimal("299.99"), Product.Category.WEAPONS, 42, Instant.now());
        var r2d2 = new Product("prod-d2-001", "R2-D2 Unit", "Astromech droid, slightly used",
                new BigDecimal("4999.00"), Product.Category.DROIDS, 5, Instant.now());
        products.put(lightsaber.id(), lightsaber);
        products.put(r2d2.id(), r2d2);
    }
}
