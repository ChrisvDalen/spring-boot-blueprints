package com.example.hexagonal.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * The domain model is the heart of the hexagon.
 * It has zero dependencies on Spring, JPA, or any framework.
 * It can be tested without starting any context.
 * Business rules live here — not in services, not in controllers.
 */
public class Order {

    private final String id;
    private final String customerEmail;
    private final String productSku;
    private final int quantity;
    private final BigDecimal totalPrice;
    private OrderStatus status;
    private final Instant createdAt;

    public Order(String customerEmail, String productSku, int quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID().toString();
        this.customerEmail = customerEmail;
        this.productSku = productSku;
        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    // Reconstitution constructor — used by the persistence adapter to rebuild from storage
    public Order(String id, String customerEmail, String productSku, int quantity,
                 BigDecimal totalPrice, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.productSku = productSku;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be confirmed, current: " + status);
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel a shipped order");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public String getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public String getProductSku() { return productSku; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public enum OrderStatus { PENDING, CONFIRMED, SHIPPED, CANCELLED }
}
