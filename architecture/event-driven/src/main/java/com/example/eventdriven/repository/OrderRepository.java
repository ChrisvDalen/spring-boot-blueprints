package com.example.eventdriven.repository;

import com.example.eventdriven.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
