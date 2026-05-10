package com.example.strategy;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;
import com.example.strategy.service.ShippingService;
import com.example.strategy.strategy.ExpressShippingStrategy;
import com.example.strategy.strategy.HyperspaceShippingStrategy;
import com.example.strategy.strategy.StandardShippingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ShippingServiceTest {

    private final ShippingService service = new ShippingService(List.of(
            new StandardShippingStrategy(),
            new ExpressShippingStrategy(),
            new HyperspaceShippingStrategy()
    ));

    private final ShippingRequest request = new ShippingRequest("Tatooine", "Coruscant", new BigDecimal("5.0"));

    @Test
    void availableCarriers_containsAllThree() {
        assertThat(service.availableCarriers()).containsExactlyInAnyOrder("standard", "express", "hyperspace");
    }

    @Test
    void quote_standard_isChepeastOfThree() {
        ShippingQuote standard = service.quote("standard", request);
        ShippingQuote express = service.quote("express", request);
        ShippingQuote hyperspace = service.quote("hyperspace", request);

        assertThat(standard.cost()).isLessThan(express.cost());
        assertThat(express.cost()).isLessThan(hyperspace.cost());
    }

    @Test
    void allQuotes_sortedByCostAscending() {
        List<ShippingQuote> quotes = service.allQuotes(request);
        for (int i = 0; i < quotes.size() - 1; i++) {
            assertThat(quotes.get(i).cost()).isLessThanOrEqualTo(quotes.get(i + 1).cost());
        }
    }

    @Test
    void quote_unknownCarrier_throws() {
        assertThatThrownBy(() -> service.quote("pigeon-post", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown carrier");
    }
}
