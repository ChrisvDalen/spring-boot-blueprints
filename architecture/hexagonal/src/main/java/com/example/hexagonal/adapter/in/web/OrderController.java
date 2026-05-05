package com.example.hexagonal.adapter.in.web;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.port.in.ManageOrderUseCase;
import com.example.hexagonal.domain.port.in.PlaceOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Web adapter — the inbound adapter that drives the application via HTTP.
 *
 * It depends on inbound PORTS (interfaces), not on the application service directly.
 * This means you could swap the entire web layer for a CLI or gRPC adapter
 * without touching any domain or application code.
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Hexagonal architecture demo — depends on ports, not implementations")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final ManageOrderUseCase manageOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase, ManageOrderUseCase manageOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.manageOrderUseCase = manageOrderUseCase;
    }

    @GetMapping
    @Operation(summary = "List all orders")
    public List<OrderResponse> list() {
        return manageOrderUseCase.findAll().stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> get(@PathVariable String id) {
        return manageOrderUseCase.findById(id)
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<OrderResponse> place(@Valid @RequestBody PlaceOrderRequest request) {
        Order order = placeOrderUseCase.place(
                request.customerEmail(), request.productSku(), request.quantity(), request.unitPrice()
        );
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(OrderResponse.from(order));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm a pending order")
    public OrderResponse confirm(@PathVariable String id) {
        return OrderResponse.from(manageOrderUseCase.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public OrderResponse cancel(@PathVariable String id) {
        return OrderResponse.from(manageOrderUseCase.cancel(id));
    }
}
