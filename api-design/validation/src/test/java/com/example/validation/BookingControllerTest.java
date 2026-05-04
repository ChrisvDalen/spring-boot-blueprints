package com.example.validation;

import com.example.validation.model.BookingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void validBooking_returns201() throws Exception {
        var request = new BookingRequest("Luke Skywalker", "tatooine-suite", "2025-06-01", "2025-06-07", 2);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void startDateAfterEndDate_failsCrossFieldValidation() throws Exception {
        var request = new BookingRequest("Han Solo", "falcon-cabin", "2025-06-10", "2025-06-01", 1);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "'', room-1, 2025-01-01, 2025-01-02, 1",   // blank guestName
            "Guest, '', 2025-01-01, 2025-01-02, 1",     // blank roomId
            "Guest, room-1, bad-date, 2025-01-02, 1",   // invalid date format
            "Guest, room-1, 2025-01-01, 2025-01-02, 0", // guestCount < 1
            "Guest, room-1, 2025-01-01, 2025-01-02, 11" // guestCount > 10
    })
    void invalidRequest_returns400(String guestName, String roomId, String start, String end, int count) throws Exception {
        var request = new BookingRequest(guestName, roomId, start, end, count);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
