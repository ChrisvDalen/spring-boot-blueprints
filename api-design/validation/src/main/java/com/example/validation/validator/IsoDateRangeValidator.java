package com.example.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class IsoDateRangeValidator implements ConstraintValidator<IsoDateRange, DateRangeHolder> {

    @Override
    public boolean isValid(DateRangeHolder value, ConstraintValidatorContext context) {
        if (value == null || value.startDate() == null || value.endDate() == null) {
            return true; // let @NotNull handle null cases
        }
        try {
            LocalDate start = LocalDate.parse(value.startDate());
            LocalDate end = LocalDate.parse(value.endDate());
            return !start.isAfter(end);
        } catch (Exception e) {
            return false;
        }
    }
}
