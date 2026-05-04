package com.example.validation.validator;

/**
 * Interface for DTOs that carry a date range, so IsoDateRangeValidator
 * can work against any DTO without being tied to a specific class.
 */
public interface DateRangeHolder {
    String startDate();
    String endDate();
}
