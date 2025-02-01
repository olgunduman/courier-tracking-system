package com.courier.tracking;

import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.dto.Store;
import com.courier.tracking.model.Courier;
import com.courier.tracking.model.CourierLocation;
import com.courier.tracking.model.StoreEntryLog;
import com.courier.tracking.repository.CourierLocationRepository;
import com.courier.tracking.repository.CourierRepository;
import com.courier.tracking.repository.StoreEntryLogRepository;
import com.courier.tracking.service.CourierLocationCommandProcessor;
import com.courier.tracking.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierLocationCommandProcessorTest {

    @Mock
    private StoreService storeService;

    @Mock
    private StoreEntryLogRepository storeEntryLogRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private CourierLocationRepository courierLocationRepository;

    @InjectMocks
    private CourierLocationCommandProcessor processor;

    private Courier courier;
    private CourierLocationRequest locationRequest;
    private Store store;

    @BeforeEach
    void setUp() {
        courier = Courier.builder()
                .courierId("courier-123")
                .build();
        locationRequest = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();
        store = Store.builder()
                .name("Store 1")
                .lat(40.7128)
                .lng(-74.0060)
                .build();
    }


    @Test
    void processLocationCommandCreatesNewCourierIfNotExists() {
        CourierLocationRequest locationDTO = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();
        when(courierRepository.findByCourierId("courier-123")).thenReturn(Optional.empty());
        when(courierRepository.save(any(Courier.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(storeService.getStores()).thenReturn(Collections.emptyList());

        processor.processLocationCommand(locationDTO);

        verify(courierRepository, times(1)).save(any(Courier.class));
        verify(courierLocationRepository, times(1)).save(any());
    }

    @Test
    void processLocationCommandUpdatesTotalDistanceForExistingCourier() {
        CourierLocationRequest locationDTO = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();
        Courier courier = Courier.builder()
                .courierId("courier-123")
                .locations(List.of(CourierLocation.builder()
                        .lat(41.7128)
                        .lng(-74.0060)
                        .build()))
                .totalDistance(0.0)
                .build();
        when(courierRepository.findByCourierId("courier-123")).thenReturn(Optional.of(courier));
        when(storeService.getStores()).thenReturn(Collections.emptyList());

        processor.processLocationCommand(locationDTO);

        assertTrue(courier.getTotalDistance() > 0);
        verify(courierLocationRepository, times(1)).save(any());
    }

    @Test
    void processLocationCommandLogsStoreEntryIfWithin100Meters() {
        CourierLocationRequest locationDTO = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();
        Courier courier = Courier.builder()
                .courierId("courier-123")
                .locations(new ArrayList<>())
                .build();
        Store store = Store.builder()
                .name("Store 1")
                .lat(40.7128)
                .lng(-74.0060)
                .build();
        when(courierRepository.findByCourierId("courier-123")).thenReturn(Optional.of(courier));
        when(storeService.getStores()).thenReturn(List.of(store));
        when(storeEntryLogRepository.findByCourierIdAndStoreName("courier-123", "Store 1")).thenReturn(Optional.empty());

        processor.processLocationCommand(locationDTO);

        verify(storeEntryLogRepository, times(1)).save(any(StoreEntryLog.class));
    }

    @Test
    void processLocationCommandDoesNotLogStoreEntryIfWithin1Minute() {
        CourierLocationRequest locationDTO = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(40.7128)
                .lng(-74.0060)
                .timestamp(LocalDateTime.now())
                .build();
        Courier courier = Courier.builder()
                .courierId("courier-123")
                .locations(new ArrayList<>())
                .build();
        Store store = Store.builder()
                .name("Store 1")
                .lat(40.7128)
                .lng(-74.0060)
                .build();
        StoreEntryLog log = StoreEntryLog.builder()
                .courierId("courier-123")
                .storeName("Store 1")
                .lastEntryTime(LocalDateTime.now().minusSeconds(30))
                .build();
        when(courierRepository.findByCourierId("courier-123")).thenReturn(Optional.of(courier));
        when(storeService.getStores()).thenReturn(List.of(store));
        when(storeEntryLogRepository.findByCourierIdAndStoreName("courier-123", "Store 1")).thenReturn(Optional.of(log));

        processor.processLocationCommand(locationDTO);

        verify(storeEntryLogRepository, times(0)).save(any(StoreEntryLog.class));
    }

}

