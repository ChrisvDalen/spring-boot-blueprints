package com.example.hexagonal.domain.port.out;

import com.example.hexagonal.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port — what the domain NEEDs from the outside world.
 *
 * The domain defines this interface. The persistence adapter implements it.
 * The domain never knows whether data lives in H2, PostgreSQL, or a HashMap —
 * that decision is made at the adapter layer, not here.
 *
 * This is Dependency Inversion: the domain owns the contract, the adapter
 * conforms to it. Not the other way around.
 */
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
}
