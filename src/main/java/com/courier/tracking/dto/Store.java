package com.courier.tracking.dto;

import lombok.*;

@Builder
public record Store (String name, double lat, double lng) {
}
