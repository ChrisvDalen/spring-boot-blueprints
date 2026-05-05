package com.example.errorhandling.advice;

import com.example.errorhandling.exception.InsufficientStockException;
import com.example.errorhandling.exception.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

/**
 * Centralised error handling using RFC 9457 Problem Details.
 *
 * Why a single @RestControllerAdvice instead of try/catch in each controller?
 * - Error handling is cross-cutting; it belongs in one place
 * - Controllers stay focused on the happy path
 * - Consistent response shape for all errors
 *
 * Why RFC 9457 ProblemDetail instead of a custom error envelope?
 * - It's an IETF standard — clients and frameworks understand it
 * - Spring Boot 3+ has native ProblemDetail support
 * - The `type` URI lets you document error categories in a stable location
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE = "https://example.com/problems";

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setType(URI.create(PROBLEM_BASE + "/order-not-found"));
        detail.setTitle("Order Not Found");
        detail.setProperty("orderId", ex.getOrderId());
        return detail;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setType(URI.create(PROBLEM_BASE + "/insufficient-stock"));
        detail.setTitle("Insufficient Stock");
        detail.setProperty("productId", ex.getProductId());
        detail.setProperty("requested", ex.getRequested());
        detail.setProperty("available", ex.getAvailable());
        return detail;
    }

    // Spring Boot 3 with spring.mvc.problemdetails.enabled=true handles
    // MethodArgumentNotValidException automatically. This override enriches
    // the response with a structured field-level error list.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Request validation failed"
        );
        detail.setType(URI.create(PROBLEM_BASE + "/validation-error"));
        detail.setTitle("Validation Error");
        detail.setProperty("errors", errors);
        return detail;
    }

    record FieldError(String field, String message) {}
}
