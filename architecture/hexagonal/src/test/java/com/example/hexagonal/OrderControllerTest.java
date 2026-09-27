package com.example.hexagonal;

import com.example.hexagonal.adapter.in.web.PlaceOrderRequest;
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
 * Web-adapter tests: verifies the HTTP contract and that domain exceptions
 * are translated to proper problem-details responses (404 / 409) rather than 500s.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private PlaceOrderRequest validRequest() {
        return new PlaceOrderRequest("luke@jedi.org", "LIGHTSABER", 2, new BigDecimal("299.99"));
    }

    private String placeOrder() throws Exception {
        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }

    @Test
    void placeOrder_returns201WithLocation() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalPrice").value(599.98));
    }

    @Test
    void getExistingOrder_returns200() throws Exception {
        String id = placeOrder();
        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("luke@jedi.org"));
    }

    @Test
    void getUnknownOrder_returns404() throws Exception {
        mockMvc.perform(get("/orders/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmOrder_transitionsToConfirmed() throws Exception {
        String id = placeOrder();
        mockMvc.perform(post("/orders/" + id + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmUnknownOrder_returns404Not500() throws Exception {
        mockMvc.perform(post("/orders/does-not-exist/confirm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void confirmAlreadyConfirmedOrder_returns409Not500() throws Exception {
        String id = placeOrder();
        mockMvc.perform(post("/orders/" + id + "/confirm")).andExpect(status().isOk());

        mockMvc.perform(post("/orders/" + id + "/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Illegal state"));
    }

    @Test
    void cancelPendingOrder_transitionsToCancelled() throws Exception {
        String id = placeOrder();
        mockMvc.perform(post("/orders/" + id + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void listOrders_returnsAll() throws Exception {
        placeOrder();
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
    }
}
