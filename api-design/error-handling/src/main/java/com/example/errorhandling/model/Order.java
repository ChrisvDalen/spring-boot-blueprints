package com.example.errorhandling.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Order(
        String id,
        String productId,
        int quantity,
        BigDecimal total,
        Instant createdAt
) {}
