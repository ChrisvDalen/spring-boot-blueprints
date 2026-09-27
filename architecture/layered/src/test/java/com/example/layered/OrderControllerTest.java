package com.example.layered;

import com.example.layered.model.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API-level tests for the layered module: HTTP status codes, problem details,
 * and the full request -> controller -> service -> repository flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private OrderRequest validRequest() {
        return new OrderRequest("luke@jedi.org", "LIGHTSABER", 2, new BigDecimal("299.99"));
    }

    private String createOrder() throws Exception {
        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }

    @Test
    void createOrder_returns201WithLocation() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getCreatedOrder_returns200() throws Exception {
        String id = createOrder();
        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("luke@jedi.org"));
    }

    @Test
    void getUnknownOrder_returns404() throws Exception {
        mockMvc.perform(get("/orders/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmOrder_transitionsStatus() throws Exception {
        String id = createOrder();
        mockMvc.perform(post("/orders/" + id + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmUnknownOrder_returns404() throws Exception {
        mockMvc.perform(post("/orders/999999/confirm"))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmAlreadyConfirmedOrder_returns409Conflict() throws Exception {
        String id = createOrder();
        mockMvc.perform(post("/orders/" + id + "/confirm")).andExpect(status().isOk());

        mockMvc.perform(post("/orders/" + id + "/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }

    @Test
    void listOrdersByEmail_returnsMatchingOrdersOnly() throws Exception {
        createOrder();
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new OrderRequest("han@falcon.org", "HYPERDRIVE", 1, new BigDecimal("5000")))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/orders").param("email", "luke@jedi.org"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerEmail").value("luke@jedi.org"));
    }

    @Test
    void createOrder_invalidEmail_returns400() throws Exception {
        var request = new OrderRequest("not-an-email", "LIGHTSABER", 1, new BigDecimal("10.00"));
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
