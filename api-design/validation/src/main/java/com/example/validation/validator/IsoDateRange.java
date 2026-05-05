package com.example.validation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that startDate is before endDate on a DTO.
 * A custom constraint for cross-field validation — something @Min/@Max can't do.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateRangeValidator.class)
@Documented
public @interface IsoDateRange {
    String message() default "startDate must be before endDate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
