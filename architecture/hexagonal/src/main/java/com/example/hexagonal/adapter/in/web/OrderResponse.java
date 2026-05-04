package com.example.hexagonal.adapter.in.web;

import com.example.hexagonal.domain.model.Order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        String id,
        String customerEmail,
        String productSku,
        int quantity,
        BigDecimal totalPrice,
        Order.OrderStatus status,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(), order.getCustomerEmail(), order.getProductSku(),
                order.getQuantity(), order.getTotalPrice(), order.getStatus(), order.getCreatedAt()
        );
    }
}
