package com.example.builder.model;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public record ReportSpecRequest(
        @NotBlank String reportType,
        LocalDate from,
        LocalDate to,
        List<String> categories,
        String groupBy,
        Boolean includeSubtotals,
        Boolean includeTax,
        Integer maxRows,
        String format
) {}
