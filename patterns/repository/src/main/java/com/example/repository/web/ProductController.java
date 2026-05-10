package com.example.repository.web;

import com.example.repository.application.ProductService;
import com.example.repository.domain.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Repository pattern demo — domain interface, two implementations")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all products")
    public List<Product> list(@RequestParam(required = false) String category) {
        return category != null ? productService.findByCategory(category) : productService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> get(@PathVariable String id) {
        return productService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Find products below stock threshold", description = "Demonstrates domain-language query method on the repository interface")
    public List<Product> lowStock(@RequestParam(defaultValue = "10") int threshold) {
        return productService.findLowStock(threshold);
    }

    @PostMapping
    @Operation(summary = "Create product")
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest req) {
        Product created = productService.create(req.name(), req.category(), req.price(), req.stock());
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{id}/reduce-stock")
    @Operation(summary = "Reduce product stock by quantity")
    public Product reduceStock(@PathVariable String id, @RequestParam int quantity) {
        return productService.reduceStock(id, quantity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
