package com.example.strategy.controller;

import com.example.strategy.model.ShippingQuote;
import com.example.strategy.model.ShippingRequest;
import com.example.strategy.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping", description = "Strategy pattern demo — Spring-wired strategies via List injection")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/carriers")
    @Operation(summary = "List available carriers", description = "Each carrier is a separate @Component — add one without touching this controller")
    public List<String> carriers() {
        return shippingService.availableCarriers();
    }

    @PostMapping("/quote/{carrierId}")
    @Operation(summary = "Get a quote from a specific carrier")
    public ShippingQuote quote(@PathVariable String carrierId, @Valid @RequestBody ShippingRequest request) {
        return shippingService.quote(carrierId, request);
    }

    @PostMapping("/quotes")
    @Operation(summary = "Get quotes from all carriers, sorted by cost")
    public List<ShippingQuote> allQuotes(@Valid @RequestBody ShippingRequest request) {
        return shippingService.allQuotes(request);
    }
}
