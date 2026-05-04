package com.example.hexagonal.application;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.port.in.ManageOrderUseCase;
import com.example.hexagonal.domain.port.in.PlaceOrderUseCase;
import com.example.hexagonal.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The application service implements the inbound ports.
 * It orchestrates domain objects and calls outbound ports.
 *
 * Notice what's NOT here:
 * - No HTTP annotations
 * - No JPA annotations
 * - No knowledge of how data is stored or how the request arrived
 *
 * This class can be tested with a fake/stub OrderRepository,
 * no Spring context required.
 */
@Service
public class OrderService implements PlaceOrderUseCase, ManageOrderUseCase {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order place(String customerEmail, String productSku, int quantity, BigDecimal unitPrice) {
        Order order = new Order(customerEmail, productSku, quantity, unitPrice);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order confirm(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        order.confirm();
        return orderRepository.save(order);
    }

    @Override
    public Order cancel(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        order.cancel();
        return orderRepository.save(order);
    }
}
