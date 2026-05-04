package com.example.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A page of results with pagination metadata")
public record PagedResponse<T>(

        @Schema(description = "Items on this page")
        List<T> content,

        @Schema(description = "Current page number (0-based)", example = "0")
        int page,

        @Schema(description = "Number of items per page", example = "20")
        int size,

        @Schema(description = "Total number of items across all pages", example = "150")
        long totalElements,

        @Schema(description = "Total number of pages", example = "8")
        int totalPages
) {
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long total) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PagedResponse<>(content, page, size, total, totalPages);
    }
}
