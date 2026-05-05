package com.example.restconventions.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ANTI-PATTERN: How NOT to design REST endpoints.
 *
 * These examples are intentionally wrong. They exist so you can see
 * exactly what problems the correct approach solves.
 */
@RestController
@RequestMapping("/anti-pattern/articles")
@Tag(name = "Anti-Pattern: Articles", description = "❌ Examples of common REST design mistakes — do NOT copy these")
public class AntiPatternArticleController {

    // ANTI-PATTERN: verb in URL — the HTTP method IS the verb
    // This is RPC-over-HTTP, not REST
    @GetMapping("/getArticle")
    @Operation(summary = "❌ ANTI-PATTERN: Verb in URL", description = "Use GET /articles/{id} instead — the HTTP verb is GET, the noun is articles")
    public Map<String, Object> getArticle(@RequestParam Long id) {
        return Map.of("id", id, "title", "Sample Article");
    }

    // ANTI-PATTERN: Using POST for everything because "it's safer"
    // This loses idempotency guarantees and confuses clients
    @PostMapping("/updateArticle")
    @Operation(summary = "❌ ANTI-PATTERN: POST for update", description = "Use PATCH /articles/{id} — PATCH communicates intent and is idempotent")
    public Map<String, String> updateArticle(@RequestBody Map<String, String> body) {
        return Map.of("status", "updated");
    }

    // ANTI-PATTERN: Returning 200 with an error in the body
    // Clients cannot use HTTP status codes to detect errors programmatically
    @GetMapping("/findArticle/{id}")
    @Operation(summary = "❌ ANTI-PATTERN: 200 with error body", description = "Return 404 with RFC 9457 Problem Details — never 200 {error: 'not found'}")
    public Map<String, Object> findArticle(@PathVariable Long id) {
        if (id > 100) {
            // ANTI-PATTERN: 200 OK with an error payload — HTTP status is lying
            return Map.of("success", false, "error", "Article not found");
        }
        return Map.of("success", true, "id", id);
    }

    // ANTI-PATTERN: Singular noun for collection resource
    // REST convention is plural nouns: /articles not /article
    @GetMapping("/article")
    @Operation(summary = "❌ ANTI-PATTERN: Singular noun for collection", description = "Use /articles — collection resources use plural nouns by convention")
    public Map<String, String> listArticlesSingular() {
        return Map.of("note", "Should be /articles (plural)");
    }
}
