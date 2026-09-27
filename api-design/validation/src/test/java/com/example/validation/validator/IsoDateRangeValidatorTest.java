package com.example.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the cross-field date-range validator — no Spring, no Hibernate.
 * Exercises the exact rule (start <= end) plus the null/format edge cases.
 */
class IsoDateRangeValidatorTest {

    private final IsoDateRangeValidator validator = new IsoDateRangeValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    private DateRangeHolder holder(String start, String end) {
        return new DateRangeHolder() {
            @Override public String startDate() { return start; }
            @Override public String endDate() { return end; }
        };
    }

    @Test
    void nullValue_isValid() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void nullDates_areIgnoredAndValid() {
        assertThat(validator.isValid(holder(null, null), context)).isTrue();
        assertThat(validator.isValid(holder("2025-01-01", null), context)).isTrue();
    }

    @Test
    void startBeforeEnd_isValid() {
        assertThat(validator.isValid(holder("2025-01-01", "2025-01-02"), context)).isTrue();
    }

    @Test
    void startEqualEnd_isValid() {
        assertThat(validator.isValid(holder("2025-01-01", "2025-01-01"), context)).isTrue();
    }

    @Test
    void startAfterEnd_isInvalid() {
        assertThat(validator.isValid(holder("2025-01-10", "2025-01-01"), context)).isFalse();
    }

    @Test
    void malformedDate_isInvalid() {
        assertThat(validator.isValid(holder("not-a-date", "2025-01-02"), context)).isFalse();
    }
}
