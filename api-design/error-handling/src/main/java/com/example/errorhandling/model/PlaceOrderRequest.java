package com.example.errorhandling.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlaceOrderRequest(
        @NotBlank String productId,
        @Min(1) int quantity
) {}
