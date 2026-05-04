package com.example.errorhandling;

import com.example.errorhandling.model.PlaceOrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void placeOrder_insufficientStock_returns409WithProblemDetail() throws Exception {
        var request = new PlaceOrderRequest("lightsaber-001", 999);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Insufficient Stock"))
                .andExpect(jsonPath("$.productId").value("lightsaber-001"))
                .andExpect(jsonPath("$.requested").value(999));
    }

    @Test
    void placeOrder_invalidQuantity_returns422WithFieldErrors() throws Exception {
        String badRequest = """
                {"productId": "item-1", "quantity": 0}
                """;
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void getOrder_notFound_returns404WithProblemDetail() throws Exception {
        mockMvc.perform(get("/orders/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order Not Found"))
                .andExpect(jsonPath("$.orderId").value("does-not-exist"));
    }

    @Test
    void placeOrder_validRequest_returns201() throws Exception {
        var request = new PlaceOrderRequest("kyber-crystal", 2);
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.productId").value("kyber-crystal"));
    }
}
