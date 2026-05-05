package com.example.hexagonal.adapter.out.persistence;

import com.example.hexagonal.domain.model.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * The JPA entity is an adapter concern, not a domain concern.
 * The domain model (Order) is pure Java — no persistence annotations.
 * Mapping between the two is the adapter's responsibility.
 */
@Entity
@Table(name = "orders")
class OrderJpaEntity {

    @Id
    private String id;
    private String customerEmail;
    private String productSku;
    private int quantity;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private Order.OrderStatus status;

    private Instant createdAt;

    protected OrderJpaEntity() {}

    static OrderJpaEntity from(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.id = order.getId();
        entity.customerEmail = order.getCustomerEmail();
        entity.productSku = order.getProductSku();
        entity.quantity = order.getQuantity();
        entity.totalPrice = order.getTotalPrice();
        entity.status = order.getStatus();
        entity.createdAt = order.getCreatedAt();
        return entity;
    }

    Order toDomain() {
        return new Order(id, customerEmail, productSku, quantity, totalPrice, status, createdAt);
    }
}
