package com.example.strategy.strategy;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class HyperspaceShippingStrategy implements ShippingStrategy {

    @Override
    public String carrierId() { return "hyperspace"; }

    @Override
    public ShippingQuote quote(ShippingRequest request) {
        // Hyperspace: flat rate, same-day, premium price
        BigDecimal cost = new BigDecimal("99.00").add(request.weightKg().multiply(new BigDecimal("10.00")));
        return new ShippingQuote("Millennium Falcon Logistics", cost,
                LocalDate.now(), "Same-day hyperspace delivery. We know.");
    }
}
