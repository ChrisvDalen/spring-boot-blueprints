package com.example.builder.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Immutable value object with many optional fields.
 * Without a builder, callers face a telescoping constructor nightmare:
 *
 *   new ReportSpec("sales", from, to, null, null, true, false, 100, null)
 *
 * Which field is which? Nobody knows without checking the constructor.
 * The builder gives every field a name at the call site.
 *
 * The constructor is package-private — only the Builder can call it.
 * This guarantees that every ReportSpec was built through validation.
 */
public final class ReportSpec {

    private final String reportType;
    private final LocalDate from;
    private final LocalDate to;
    private final List<String> categories;
    private final String groupBy;
    private final boolean includeSubtotals;
    private final boolean includeTax;
    private final int maxRows;
    private final String format;

    ReportSpec(Builder builder) {
        this.reportType = builder.reportType;
        this.from = builder.from;
        this.to = builder.to;
        this.categories = builder.categories != null ? List.copyOf(builder.categories) : List.of();
        this.groupBy = builder.groupBy;
        this.includeSubtotals = builder.includeSubtotals;
        this.includeTax = builder.includeTax;
        this.maxRows = builder.maxRows;
        this.format = builder.format;
    }

    public String getReportType()      { return reportType; }
    public LocalDate getFrom()         { return from; }
    public LocalDate getTo()           { return to; }
    public List<String> getCategories(){ return categories; }
    public String getGroupBy()         { return groupBy; }
    public boolean isIncludeSubtotals(){ return includeSubtotals; }
    public boolean isIncludeTax()      { return includeTax; }
    public int getMaxRows()            { return maxRows; }
    public String getFormat()          { return format; }

    @Override
    public String toString() {
        return "ReportSpec{type=%s, from=%s, to=%s, categories=%s, groupBy=%s, subtotals=%b, tax=%b, maxRows=%d, format=%s}"
                .formatted(reportType, from, to, categories, groupBy, includeSubtotals, includeTax, maxRows, format);
    }

    public static Builder builder(String reportType) {
        return new Builder(reportType);
    }

    public static final class Builder {

        private final String reportType;
        private LocalDate from;
        private LocalDate to;
        private List<String> categories;
        private String groupBy;
        private boolean includeSubtotals = false;
        private boolean includeTax = true;
        private int maxRows = 1000;
        private String format = "JSON";

        private Builder(String reportType) {
            this.reportType = Objects.requireNonNull(reportType, "reportType required");
        }

        public Builder from(LocalDate from)            { this.from = from; return this; }
        public Builder to(LocalDate to)                { this.to = to; return this; }
        public Builder categories(List<String> cats)   { this.categories = cats; return this; }
        public Builder groupBy(String groupBy)         { this.groupBy = groupBy; return this; }
        public Builder includeSubtotals(boolean v)     { this.includeSubtotals = v; return this; }
        public Builder includeTax(boolean v)           { this.includeTax = v; return this; }
        public Builder maxRows(int maxRows)            { this.maxRows = maxRows; return this; }
        public Builder format(String format)           { this.format = format; return this; }

        public ReportSpec build() {
            if (from != null && to != null && from.isAfter(to)) {
                throw new IllegalStateException("'from' must not be after 'to'");
            }
            if (maxRows < 1 || maxRows > 100_000) {
                throw new IllegalStateException("maxRows must be between 1 and 100,000");
            }
            return new ReportSpec(this);
        }
    }
}
