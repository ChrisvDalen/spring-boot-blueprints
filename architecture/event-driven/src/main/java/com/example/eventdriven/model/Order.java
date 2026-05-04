package com.example.eventdriven.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;
    private String customerEmail;
    private String productSku;
    private int quantity;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    protected Order() {}

    public Order(String id, String customerEmail, String productSku, int quantity, BigDecimal totalPrice) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.productSku = productSku;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void confirm() { this.status = OrderStatus.CONFIRMED; }
    public void ship()    { this.status = OrderStatus.SHIPPED; }

    public String getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public String getProductSku() { return productSku; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public enum OrderStatus { PENDING, CONFIRMED, SHIPPED }
}
