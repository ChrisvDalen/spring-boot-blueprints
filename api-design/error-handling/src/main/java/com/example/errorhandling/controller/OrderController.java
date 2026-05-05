package com.example.errorhandling.controller;

import com.example.errorhandling.exception.InsufficientStockException;
import com.example.errorhandling.exception.OrderNotFoundException;
import com.example.errorhandling.model.Order;
import com.example.errorhandling.model.PlaceOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Demonstrates RFC 9457 Problem Details error handling")
public class OrderController {

    private static final int STOCK_LIMIT = 10;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    @PostMapping
    @Operation(
            summary = "Place an order",
            description = "Throws InsufficientStockException (409 Conflict) if quantity > 10"
    )
    @ApiResponse(responseCode = "201", description = "Order placed")
    @ApiResponse(responseCode = "409", description = "Insufficient stock",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        if (request.quantity() > STOCK_LIMIT) {
            throw new InsufficientStockException(request.productId(), request.quantity(), STOCK_LIMIT);
        }
        Order order = new Order(
                UUID.randomUUID().toString(),
                request.productId(),
                request.quantity(),
                BigDecimal.valueOf(request.quantity() * 9.99),
                Instant.now()
        );
        orders.put(order.id(), order);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(order.id()).toUri();
        return ResponseEntity.created(location).body(order);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public Order getOrder(@PathVariable String id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new OrderNotFoundException(id);
        }
        return order;
    }
}
