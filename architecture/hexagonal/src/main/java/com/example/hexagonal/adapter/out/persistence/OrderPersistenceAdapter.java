package com.example.hexagonal.adapter.out.persistence;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.port.out.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Outbound adapter — implements the domain's outbound port using JPA.
 *
 * The domain port (OrderRepository) knows nothing about JPA.
 * This class is the only place that knows about JPA, H2, and entity mapping.
 * Swap this for a MongoDB adapter and nothing in the domain or application changes.
 */
@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(OrderJpaEntity.from(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(String id) {
        return jpaRepository.findById(id).map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream().map(OrderJpaEntity::toDomain).toList();
    }
}
