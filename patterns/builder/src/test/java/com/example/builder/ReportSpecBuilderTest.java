package com.example.builder;

import com.example.builder.model.ReportSpec;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ReportSpecBuilderTest {

    @Test
    void minimalBuild_usesDefaults() {
        ReportSpec spec = ReportSpec.builder("sales").build();

        assertThat(spec.getReportType()).isEqualTo("sales");
        assertThat(spec.getMaxRows()).isEqualTo(1000);
        assertThat(spec.getFormat()).isEqualTo("JSON");
        assertThat(spec.isIncludeTax()).isTrue();
        assertThat(spec.getCategories()).isEmpty();
    }

    @Test
    void fullBuild_allFieldsSet() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);

        ReportSpec spec = ReportSpec.builder("revenue")
                .from(from).to(to)
                .categories(List.of("WEAPONS", "DROIDS"))
                .groupBy("category")
                .includeSubtotals(true)
                .includeTax(false)
                .maxRows(500)
                .format("CSV")
                .build();

        assertThat(spec.getFrom()).isEqualTo(from);
        assertThat(spec.getTo()).isEqualTo(to);
        assertThat(spec.getCategories()).containsExactly("WEAPONS", "DROIDS");
        assertThat(spec.getGroupBy()).isEqualTo("category");
        assertThat(spec.isIncludeSubtotals()).isTrue();
        assertThat(spec.isIncludeTax()).isFalse();
        assertThat(spec.getMaxRows()).isEqualTo(500);
        assertThat(spec.getFormat()).isEqualTo("CSV");
    }

    @Test
    void build_fromAfterTo_throwsIllegalState() {
        assertThatThrownBy(() -> ReportSpec.builder("sales")
                .from(LocalDate.of(2025, 6, 1))
                .to(LocalDate.of(2025, 1, 1))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("from");
    }

    @Test
    void build_maxRowsOutOfRange_throws() {
        assertThatThrownBy(() -> ReportSpec.builder("sales").maxRows(0).build())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> ReportSpec.builder("sales").maxRows(200_000).build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void categories_areImmutableCopy() {
        var cats = new java.util.ArrayList<>(List.of("A", "B"));
        ReportSpec spec = ReportSpec.builder("x").categories(cats).build();
        cats.add("C");
        assertThat(spec.getCategories()).hasSize(2);
    }
}
