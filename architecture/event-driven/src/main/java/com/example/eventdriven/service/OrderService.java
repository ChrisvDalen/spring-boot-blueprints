package com.example.eventdriven.service;

import com.example.eventdriven.event.OrderConfirmedEvent;
import com.example.eventdriven.event.OrderPlacedEvent;
import com.example.eventdriven.event.OrderShippedEvent;
import com.example.eventdriven.model.Order;
import com.example.eventdriven.model.PlaceOrderRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The service publishes events after state changes.
 * It does NOT call notification services, inventory services, or audit logs directly.
 * Those concerns belong to listeners — the service stays focused on the order domain.
 *
 * Decoupling via events means:
 * - New side effects (e.g. loyalty points) require zero changes to this class
 * - Listeners can be tested in isolation
 * - The service remains readable and single-purpose
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderJpaRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderJpaRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Optional<Order> findById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public Order place(PlaceOrderRequest request) {
        BigDecimal total = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        Order order = new Order(UUID.randomUUID().toString(), request.customerEmail(),
                request.productSku(), request.quantity(), total);
        Order saved = repository.save(order);

        // Event published AFTER commit (transaction is still open here — Spring fires
        // ApplicationEvent synchronously within the same transaction by default).
        // For guaranteed-delivery semantics, pair this with the Transactional Outbox pattern.
        eventPublisher.publishEvent(new OrderPlacedEvent(
                saved.getId(), saved.getCustomerEmail(), saved.getProductSku(),
                saved.getQuantity(), saved.getTotalPrice(), Instant.now()
        ));

        return saved;
    }

    @Transactional
    public Order confirm(String id) {
        Order order = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
        order.confirm();
        Order saved = repository.save(order);

        eventPublisher.publishEvent(new OrderConfirmedEvent(saved.getId(), saved.getCustomerEmail(), Instant.now()));
        return saved;
    }

    @Transactional
    public Order ship(String id, String trackingNumber) {
        Order order = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
        order.ship();
        Order saved = repository.save(order);

        eventPublisher.publishEvent(new OrderShippedEvent(saved.getId(), saved.getCustomerEmail(), trackingNumber, Instant.now()));
        return saved;
    }

    @Repository
    interface OrderJpaRepository extends JpaRepository<Order, String> {}
}
