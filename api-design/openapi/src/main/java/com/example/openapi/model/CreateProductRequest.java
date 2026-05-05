package com.example.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request body for creating a new product")
public record CreateProductRequest(

        @Schema(description = "Product name", example = "Lightsaber (Blue)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "Product description", example = "Standard-issue Jedi weapon")
        String description,

        @Schema(description = "Price in Galactic Credits", example = "299.99", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @DecimalMin("0.01")
        BigDecimal price,

        @Schema(description = "Product category", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Product.Category category,

        @Schema(description = "Initial stock level", example = "100")
        @Min(0)
        int stock
) {}
