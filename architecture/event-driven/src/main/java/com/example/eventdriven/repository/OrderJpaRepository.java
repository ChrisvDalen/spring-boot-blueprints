package com.example.eventdriven.repository;

import com.example.eventdriven.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, String> {}
