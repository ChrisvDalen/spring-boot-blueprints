package com.example.builder.controller;

import com.example.builder.model.ReportSpec;
import com.example.builder.model.ReportSpecRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Builder pattern demo — fluent construction with validation in build()")
public class ReportController {

    @PostMapping("/spec")
    @Operation(
            summary = "Build a report spec",
            description = "Demonstrates the Builder translating a flat request into a validated, immutable ReportSpec"
    )
    public ReportSpec buildSpec(@Valid @RequestBody ReportSpecRequest request) {
        ReportSpec.Builder builder = ReportSpec.builder(request.reportType());

        if (request.from() != null)             builder.from(request.from());
        if (request.to() != null)               builder.to(request.to());
        if (request.categories() != null)       builder.categories(request.categories());
        if (request.groupBy() != null)          builder.groupBy(request.groupBy());
        if (request.includeSubtotals() != null) builder.includeSubtotals(request.includeSubtotals());
        if (request.includeTax() != null)       builder.includeTax(request.includeTax());
        if (request.maxRows() != null)          builder.maxRows(request.maxRows());
        if (request.format() != null)           builder.format(request.format());

        return builder.build();
    }

    @GetMapping("/spec/example")
    @Operation(summary = "Return a hardcoded example showing fluent builder syntax")
    public ReportSpec example() {
        return ReportSpec.builder("sales")
                .from(java.time.LocalDate.now().minusMonths(1))
                .to(java.time.LocalDate.now())
                .categories(java.util.List.of("WEAPONS", "DROIDS"))
                .groupBy("category")
                .includeSubtotals(true)
                .includeTax(true)
                .maxRows(500)
                .format("CSV")
                .build();
    }
}
