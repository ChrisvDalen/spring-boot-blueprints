package com.example.layered.controller;

import com.example.layered.model.OrderRequest;
import com.example.layered.model.OrderResponse;
import com.example.layered.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Controller layer: HTTP concerns only.
 * Translates HTTP requests into service calls and service results into HTTP responses.
 * No business logic lives here — it delegates entirely to OrderService.
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Layered architecture demo — order management")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "List all orders")
    public List<OrderResponse> list() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @GetMapping(params = "email")
    @Operation(summary = "List orders by customer email")
    public List<OrderResponse> byCustomer(@RequestParam String email) {
        return orderService.findByCustomer(email);
    }

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse created = orderService.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm a pending order")
    public OrderResponse confirm(@PathVariable Long id) {
        return orderService.confirm(id);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public OrderResponse cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }
}
