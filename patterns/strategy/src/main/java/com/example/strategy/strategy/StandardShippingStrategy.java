package com.example.strategy.strategy;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class StandardShippingStrategy implements ShippingStrategy {

    @Override
    public String carrierId() { return "standard"; }

    @Override
    public ShippingQuote quote(ShippingRequest request) {
        BigDecimal cost = new BigDecimal("5.00").add(request.weightKg().multiply(new BigDecimal("1.20")));
        return new ShippingQuote("Standard Galactic Post", cost,
                LocalDate.now().plusDays(7), "Economy ground shipping, 5-7 business days");
    }
}
