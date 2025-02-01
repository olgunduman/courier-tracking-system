package com.courier.tracking.mapper;

import com.courier.tracking.dto.StoreEntryLogDto;
import com.courier.tracking.model.Courier;
import com.courier.tracking.model.StoreEntryLog;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class StoreEntryLogMapper {

    public static StoreEntryLog getEntryLog(Courier courier, String storeName, LocalDateTime timestamp) {
        return StoreEntryLog.builder().courierId(courier.getCourierId()).storeName(storeName).lastEntryTime(timestamp).build();
    }

}
