package com.example.restconventions.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArticleRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String body,
        @NotBlank String authorId
) {}
