package com.example.hexagonal.adapter.in.web;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PlaceOrderRequest(
        @NotBlank @Email String customerEmail,
        @NotBlank String productSku,
        @Min(1) int quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice
) {}
