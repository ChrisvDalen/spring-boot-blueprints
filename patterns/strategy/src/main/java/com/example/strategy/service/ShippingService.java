package com.example.strategy.service;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;
import com.example.strategy.strategy.ShippingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Spring injects ALL ShippingStrategy beans as a List.
 * We index them by carrierId() into a Map for O(1) lookup.
 *
 * Adding a new carrier requires only a new @Component class.
 * This class never changes — it's closed for modification, open for extension.
 * That's the Open/Closed Principle, enabled by the Strategy pattern.
 */
@Service
public class ShippingService {

    private final Map<String, ShippingStrategy> strategies;

    public ShippingService(List<ShippingStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(ShippingStrategy::carrierId, Function.identity()));
    }

    public List<String> availableCarriers() {
        return List.copyOf(strategies.keySet());
    }

    public ShippingQuote quote(String carrierId, ShippingRequest request) {
        ShippingStrategy strategy = strategies.get(carrierId);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown carrier: " + carrierId
                    + ". Available: " + strategies.keySet());
        }
        return strategy.quote(request);
    }

    public List<ShippingQuote> allQuotes(ShippingRequest request) {
        return strategies.values().stream()
                .map(s -> s.quote(request))
                .sorted(java.util.Comparator.comparing(ShippingQuote::cost))
                .toList();
    }
}
