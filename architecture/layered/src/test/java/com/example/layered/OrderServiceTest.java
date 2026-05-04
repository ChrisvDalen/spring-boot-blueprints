package com.example.layered;

import com.example.layered.exception.OrderNotFoundException;
import com.example.layered.model.Order;
import com.example.layered.model.OrderRequest;
import com.example.layered.model.OrderResponse;
import com.example.layered.repository.OrderRepository;
import com.example.layered.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void createOrder_persistsWithPendingStatus() {
        var request = new OrderRequest("luke@jedi.org", "LIGHTSABER-BLUE", 1, new BigDecimal("299.99"));
        OrderResponse response = orderService.create(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.status()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(response.totalPrice()).isEqualByComparingTo("299.99");
    }

    @Test
    void confirmOrder_changesStatusToConfirmed() {
        var request = new OrderRequest("leia@senate.org", "BLASTER-DL44", 2, new BigDecimal("150.00"));
        OrderResponse created = orderService.create(request);

        OrderResponse confirmed = orderService.confirm(created.id());
        assertThat(confirmed.status()).isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test
    void confirmOrder_alreadyConfirmed_throwsIllegalState() {
        var request = new OrderRequest("han@falcon.org", "HYPERDRIVE", 1, new BigDecimal("9999.00"));
        OrderResponse created = orderService.create(request);
        orderService.confirm(created.id());

        assertThatThrownBy(() -> orderService.confirm(created.id()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findById_notFound_throwsOrderNotFoundException() {
        assertThatThrownBy(() -> orderService.findById(Long.MAX_VALUE))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void cancelOrder_pendingOrder_succeeds() {
        var request = new OrderRequest("yoda@dagobah.org", "WALKING-STICK", 1, new BigDecimal("5.00"));
        OrderResponse created = orderService.create(request);

        OrderResponse cancelled = orderService.cancel(created.id());
        assertThat(cancelled.status()).isEqualTo(Order.OrderStatus.CANCELLED);
    }
}
