package com.courier.tracking;

import com.courier.tracking.controller.CourierController;
import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.dto.CourierResponse;
import com.courier.tracking.service.CourierQueueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CourierController.class)
class CourierControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierQueueService courierQueueService;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Test
    void testUpdateLocation_Success() throws Exception {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();
        doNothing().when(courierQueueService).enqueueLocation(any(CourierLocationRequest.class));

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Location updated successfully."));

        verify(courierQueueService, times(1)).enqueueLocation(any(CourierLocationRequest.class));
    }

    @Test
    void testUpdateLocation_InvalidRequest() throws Exception {
        CourierLocationRequest request = CourierLocationRequest
                .builder()
                .courierId("")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetTotalDistance_Success() throws Exception {
        when(courierQueueService.getTotalTravelDistance("courier-789"))
                .thenReturn(new CourierResponse(1250.57, 1.25));

        mockMvc.perform(get("/api/couriers/courier-789/distance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMeters").value(1250.57))
                .andExpect(jsonPath("$.kilometers").value(1.25));

        verify(courierQueueService, times(1)).getTotalTravelDistance("courier-789");
    }


    @Test
    void testGetTotalDistance_EmptyCourier() throws Exception {
        when(courierQueueService.getTotalTravelDistance("unknown-courier"))
                .thenReturn(new CourierResponse(0.0, 0.0));

        mockMvc.perform(get("/api/couriers/unknown-courier/distance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMeters").value(0.0))
                .andExpect(jsonPath("$.kilometers").value(0.0));

        verify(courierQueueService, times(1)).getTotalTravelDistance("unknown-courier");
    }
}
