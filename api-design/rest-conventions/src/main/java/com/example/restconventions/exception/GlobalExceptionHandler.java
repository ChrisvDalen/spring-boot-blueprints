package com.example.restconventions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE = "https://example.com/problems";

    @ExceptionHandler(ArticleNotFoundException.class)
    public ProblemDetail handleNotFound(ArticleNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setType(URI.create(PROBLEM_BASE + "/article-not-found"));
        detail.setTitle("Article Not Found");
        return detail;
    }
}
