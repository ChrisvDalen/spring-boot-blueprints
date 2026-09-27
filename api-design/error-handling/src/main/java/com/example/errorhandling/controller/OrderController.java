package com.example.errorhandling.controller;

import com.example.errorhandling.model.Order;
import com.example.errorhandling.model.PlaceOrderRequest;
import com.example.errorhandling.service.OrderService;
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

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Demonstrates RFC 9457 Problem Details error handling")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
            summary = "Place an order",
            description = "Throws InsufficientStockException (409 Conflict) if quantity > " + OrderService.STOCK_LIMIT
    )
    @ApiResponse(responseCode = "201", description = "Order placed")
    @ApiResponse(responseCode = "409", description = "Insufficient stock",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        Order order = orderService.place(request);
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
        return orderService.get(id);
    }
}
