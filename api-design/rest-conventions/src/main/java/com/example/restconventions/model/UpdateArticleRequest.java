package com.example.restconventions.model;

import jakarta.validation.constraints.Size;

public record UpdateArticleRequest(
        @Size(max = 200) String title,
        String body
) {}
