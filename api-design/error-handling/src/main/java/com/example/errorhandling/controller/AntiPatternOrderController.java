package com.example.errorhandling.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ANTI-PATTERN: Common error handling mistakes.
 * These are intentionally wrong. Study them to understand what Problem Details solves.
 */
@RestController
@RequestMapping("/anti-pattern/orders")
@Tag(name = "Anti-Pattern: Orders", description = "❌ Error handling anti-patterns — do NOT copy these")
public class AntiPatternOrderController {

    // ANTI-PATTERN: Custom error envelope instead of RFC 9457
    // Every team invents their own {"success": false, "errorCode": "..."} schema.
    // Clients must learn each one individually. No tooling standardisation.
    @GetMapping("/{id}")
    @Operation(summary = "❌ ANTI-PATTERN: Custom error envelope", description = "Use ProblemDetail (RFC 9457) — standard, tooling-friendly, no reinvention")
    public Map<String, Object> getOrder(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        if ("not-found".equals(id)) {
            // ANTI-PATTERN: 200 OK with success:false
            response.put("success", false);
            response.put("errorCode", "ORDER_NOT_FOUND");
            response.put("errorMessage", "Order not found");
        } else {
            response.put("success", true);
            response.put("data", Map.of("id", id));
        }
        return response;
    }

    // ANTI-PATTERN: Swallowing exceptions and returning empty/null
    // The caller has no idea what went wrong. Debugging is a nightmare.
    @PostMapping
    @Operation(summary = "❌ ANTI-PATTERN: Swallowed exception", description = "Never catch-and-return-null — propagate to @ExceptionHandler with meaningful context")
    public Map<String, Object> placeOrder(@RequestBody Map<String, Object> body) {
        try {
            if (body.get("quantity") == null) {
                throw new IllegalArgumentException("quantity required");
            }
            return Map.of("id", "some-id");
        } catch (Exception e) {
            // ANTI-PATTERN: swallowed — caller gets 200 with null body
            return null;
        }
    }

    // ANTI-PATTERN: Exposing stack traces to clients
    // Stack traces leak implementation details, aid attackers,
    // and are useless to API consumers.
    @GetMapping("/stacktrace/{id}")
    @Operation(summary = "❌ ANTI-PATTERN: Stack trace in response", description = "Never return stack traces to clients — log them server-side and return a correlation ID")
    public Map<String, Object> leakingStackTrace(@PathVariable String id) {
        try {
            throw new RuntimeException("Something broke internally");
        } catch (RuntimeException e) {
            // ANTI-PATTERN: returning the stack trace in the API response
            return Map.of(
                    "error", e.getMessage(),
                    "stackTrace", e.getStackTrace()[0].toString()
            );
        }
    }
}
