package com.example.eventdriven.controller;

import com.example.eventdriven.model.Order;
import com.example.eventdriven.model.PlaceOrderRequest;
import com.example.eventdriven.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Event-driven architecture demo — observers react to domain events")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "List all orders")
    public List<Order> list() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<Order> get(@PathVariable String id) {
        return orderService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Place order — fires OrderPlacedEvent to notification, inventory, and audit listeners")
    public ResponseEntity<Order> place(@Valid @RequestBody PlaceOrderRequest request) {
        Order order = orderService.place(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(order);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm order — fires OrderConfirmedEvent")
    public Order confirm(@PathVariable String id) {
        return orderService.confirm(id);
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "Ship order — fires OrderShippedEvent")
    public Order ship(@PathVariable String id, @RequestParam String trackingNumber) {
        return orderService.ship(id, trackingNumber);
    }
}
