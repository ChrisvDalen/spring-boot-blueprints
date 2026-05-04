package com.example.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "A product available in the Galactic Republic catalogue")
public record Product(

        @Schema(description = "Unique product identifier", example = "prod-abc-123")
        String id,

        @Schema(description = "Human-readable product name", example = "Lightsaber (Blue)")
        String name,

        @Schema(description = "Detailed product description")
        String description,

        @Schema(description = "Price in Galactic Credits", example = "299.99")
        BigDecimal price,

        @Schema(description = "Product category", example = "WEAPONS")
        Category category,

        @Schema(description = "Current stock level", example = "42")
        int stock,

        @Schema(description = "Timestamp when the product was added to the catalogue")
        Instant createdAt
) {
    public enum Category {
        WEAPONS, VEHICLES, DROIDS, APPAREL, FOOD
    }
}
