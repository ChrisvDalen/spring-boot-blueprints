package com.example.hexagonal.domain.port.in;

import com.example.hexagonal.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port for order lifecycle management.
 * One interface per use-case cohesion keeps this fine-grained and testable.
 */
public interface ManageOrderUseCase {
    Optional<Order> findById(String id);
    List<Order> findAll();
    Order confirm(String id);
    Order cancel(String id);
}
