package com.example.strategy.strategy;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;

/**
 * The Strategy interface — one method, multiple implementations.
 *
 * Each implementation encapsulates a different pricing algorithm.
 * The caller (ShippingService) knows only this interface; it never
 * reaches inside any strategy to see how the price is calculated.
 *
 * Adding a new carrier = adding a new class. Zero changes to existing code.
 */
public interface ShippingStrategy {
    String carrierId();
    ShippingQuote quote(ShippingRequest request);
}
