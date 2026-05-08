package com.example.repository.web;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Min(0) int stock
) {}
