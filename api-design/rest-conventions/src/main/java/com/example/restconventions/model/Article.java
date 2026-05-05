package com.example.restconventions.model;

import java.time.Instant;

public record Article(
        Long id,
        String title,
        String body,
        String authorId,
        Instant publishedAt
) {}
