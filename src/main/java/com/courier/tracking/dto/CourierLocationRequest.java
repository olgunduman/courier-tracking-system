package com.courier.tracking.dto;

import com.courier.tracking.model.Courier;
import com.courier.tracking.model.CourierLocation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourierLocationRequest {
    private String courierId;
    private LocalDateTime timestamp;
    private double lat;
    private double lng;

    public CourierLocation toEntity(Courier courier) {
        return CourierLocation.builder()
                .courier(courier)
                .timestamp(this.timestamp)
                .lat(this.lat)
                .lng(this.lng)
                .build();
    }
}