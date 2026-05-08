package com.example.strategy.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShippingQuote(
        String carrier,
        BigDecimal cost,
        LocalDate estimatedDelivery,
        String description
) {}
