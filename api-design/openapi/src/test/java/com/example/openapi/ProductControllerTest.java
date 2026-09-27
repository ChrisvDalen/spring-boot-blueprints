package com.example.openapi;

import com.example.openapi.model.CreateProductRequest;
import com.example.openapi.model.PagedResponse;
import com.example.openapi.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void openApiSpecIsAvailable() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Galactic Product Catalogue API"));
    }

    @Test
    void swaggerUiRedirectsToIndex() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void listProducts_returnsSeedData() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void createProduct_returns201() throws Exception {
        var request = new CreateProductRequest("Death Star Plans", "Slightly sensitive", new BigDecimal("1.00"),
                Product.Category.WEAPONS, 1);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void getProduct_seedItem_returns200() throws Exception {
        mockMvc.perform(get("/products/prod-ls-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lightsaber (Blue)"));
    }

    @Test
    void getProduct_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/products/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listProducts_pageSizeSplitsResults() throws Exception {
        MvcResult result = mockMvc.perform(get("/products").param("size", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andReturn();

        var page = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<PagedResponse<Product>>() {});
        // size=1 means exactly one item per page, and one page per element
        assertThat(page.content()).hasSize(1);
        assertThat(page.totalPages()).isEqualTo(page.totalElements());
    }

    @Test
    void listProducts_pageBeyondLast_returnsEmptyContent() throws Exception {
        mockMvc.perform(get("/products").param("page", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void listProducts_categoryFilterReducesResults() throws Exception {
        // Only the seeded DROIDS item belongs to that category
        mockMvc.perform(get("/products").param("category", "DROIDS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].category").value("DROIDS"));
    }

    @Test
    void createProduct_negativePrice_returns400() throws Exception {
        var request = new CreateProductRequest("Ripper", null, new java.math.BigDecimal("-1.00"),
                Product.Category.WEAPONS, 1);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_missingName_returns400() throws Exception {
        String badJson = """
                {"price": 10.00, "category": "WEAPONS", "stock": 1}
                """;
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }
}
