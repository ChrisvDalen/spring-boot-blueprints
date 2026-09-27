package com.example.hexagonal.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * Maps the domain/application exceptions that bubble up through the use cases
 * to an HTTP problem-details response.
 *
 * The domain is allowed to throw plain exceptions (it knows nothing about HTTP).
 * The web adapter is the place where those exceptions are translated to HTTP.
 * This keeps the domain and application layers transport-agnostic.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE = "https://example.com/problems";

    /** "Order not found" raised by OrderService.confirm/cancel for an unknown id. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleNotFound(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create(PROBLEM_BASE + "/not-found"));
        pd.setTitle("Resource not found");
        return pd;
    }

    /** "Only PENDING orders can be confirmed" / "Cannot cancel a shipped order". */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create(PROBLEM_BASE + "/illegal-state"));
        pd.setTitle("Illegal state");
        return pd;
    }
}
