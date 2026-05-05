package com.example.hexagonal.domain.port.in;

import com.example.hexagonal.domain.model.Order;

import java.math.BigDecimal;

/**
 * Inbound port — what the application can DO.
 *
 * This interface is defined by the domain. The web adapter (controller)
 * depends on this interface, not on any implementation. The domain
 * dictates what callers can ask of it.
 */
public interface PlaceOrderUseCase {
    Order place(String customerEmail, String productSku, int quantity, BigDecimal unitPrice);
}
