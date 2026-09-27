package com.example.errorhandling.service;

import com.example.errorhandling.exception.InsufficientStockException;
import com.example.errorhandling.exception.OrderNotFoundException;
import com.example.errorhandling.model.Order;
import com.example.errorhandling.model.PlaceOrderRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer owns the order business logic: the stock-limit rule and
 * order construction. The controller only translates HTTP <-> service.
 * Extracting this keeps the controller thin and makes the rule unit-testable
 * without Spring or HTTP.
 */
@Service
public class OrderService {

    public static final int STOCK_LIMIT = 10;
    private static final BigDecimal UNIT_PRICE = BigDecimal.valueOf(9.99);

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Order place(PlaceOrderRequest request) {
        if (request.quantity() > STOCK_LIMIT) {
            throw new InsufficientStockException(request.productId(), request.quantity(), STOCK_LIMIT);
        }
        Order order = new Order(
                UUID.randomUUID().toString(),
                request.productId(),
                request.quantity(),
                BigDecimal.valueOf(request.quantity()).multiply(UNIT_PRICE),
                Instant.now()
        );
        orders.put(order.id(), order);
        return order;
    }

    public Order get(String id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new OrderNotFoundException(id);
        }
        return order;
    }
}
