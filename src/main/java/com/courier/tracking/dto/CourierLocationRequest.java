package com.courier.tracking.dto;

import com.courier.tracking.model.Courier;
import com.courier.tracking.model.CourierLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourierLocationRequest {
    @NotBlank(message = "Courier id is required")
    private String courierId;
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    private double lat;
    private double lng;

    public CourierLocation toEntity(Courier courier) {
        return CourierLocation.builder().courier(courier).timestamp(this.timestamp).lat(this.lat).lng(this.lng).build();
    }
}