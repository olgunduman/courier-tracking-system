package com.courier.tracking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StoreEntryLogDto {
    
    private String courierId;
    private String storeName;
    private LocalDateTime lastEntryTime;
}
