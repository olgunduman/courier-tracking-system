package com.courier.tracking.dto;

import lombok.Builder;

@Builder
public record CourierResponse(double totalMeters, double kilometers) {
}