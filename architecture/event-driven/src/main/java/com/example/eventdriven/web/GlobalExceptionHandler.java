package com.example.eventdriven.web;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * Maps the exceptions thrown by the service (which know nothing about HTTP)
 * to a problem-details response. OrderService throws IllegalArgumentException
 * for an unknown order id — the web adapter translates that to a 404.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE = "https://example.com/problems";

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleNotFound(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create(PROBLEM_BASE + "/not-found"));
        pd.setTitle("Resource not found");
        return pd;
    }
}
