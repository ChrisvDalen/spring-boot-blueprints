package com.example.layered.service;

import com.example.layered.exception.OrderNotFoundException;
import com.example.layered.model.Order;
import com.example.layered.model.OrderRequest;
import com.example.layered.model.OrderResponse;
import com.example.layered.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * The service layer owns business logic and transaction boundaries.
 * It knows nothing about HTTP, JSON, or how data is stored — those
 * concerns belong to the controller and repository layers respectively.
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<OrderResponse> findByCustomer(String email) {
        return orderRepository.findByCustomerEmail(email).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        BigDecimal total = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        Order order = new Order(request.customerEmail(), request.productSku(), request.quantity(), total);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse confirm(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.confirm();
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.cancel();
        return OrderResponse.from(orderRepository.save(order));
    }
}
