package com.example.strategy.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ShippingRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull @DecimalMin("0.01") BigDecimal weightKg
) {}
