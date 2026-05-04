package com.example.layered.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank @Email String customerEmail,
        @NotBlank String productSku,
        @Min(1) int quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice
) {}
