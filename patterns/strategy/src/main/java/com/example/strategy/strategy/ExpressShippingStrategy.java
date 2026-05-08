package com.example.strategy.strategy;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ExpressShippingStrategy implements ShippingStrategy {

    @Override
    public String carrierId() { return "express"; }

    @Override
    public ShippingQuote quote(ShippingRequest request) {
        BigDecimal cost = new BigDecimal("15.00").add(request.weightKg().multiply(new BigDecimal("3.50")));
        return new ShippingQuote("Kessel Express", cost,
                LocalDate.now().plusDays(2), "Express delivery, 1-2 business days");
    }
}
