package com.courier.tracking;


import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.dto.CourierResponse;
import com.courier.tracking.service.CourierLocationCommandProcessor;
import com.courier.tracking.service.CourierQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierQueueServiceTest {

    @Mock
    private CourierLocationCommandProcessor commandProcessor;

    @InjectMocks
    private CourierQueueService courierQueueService;

    @BeforeEach
    void setUp() {
        courierQueueService.executorService = Executors.newFixedThreadPool(4);
        courierQueueService.locationQueue = new LinkedBlockingQueue<>(1000);
    }

    // ✅ 1. Lokasyon ekleme (Başarılı)
    @Test
    void testEnqueueLocation_Success() {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-123")
                .lat(41.0082)
                .lng(28.9784)
                .build();

        assertDoesNotThrow(() -> courierQueueService.enqueueLocation(request));

        // Kuyruğa eklendiğini doğrula
        assertFalse(courierQueueService.locationQueue.isEmpty());
    }

    // ❌ 2. Lokasyon ekleme (Başarısız - InterruptedException)
    @Test
    void testEnqueueLocation_InterruptedException() throws InterruptedException {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-456")
                .lat(41.0082)
                .lng(28.9784)
                .build();

        BlockingQueue<CourierLocationRequest> mockQueue = mock(BlockingQueue.class);
        doThrow(new InterruptedException()).when(mockQueue).put(request);

        courierQueueService.locationQueue = mockQueue;

        assertDoesNotThrow(() -> courierQueueService.enqueueLocation(request));
    }

    // ✅ 3. Mesafe hesaplama (Başarılı)
    @Test
    void testGetTotalTravelDistance_Success() {
        String courierId = "courier-789";
        when(commandProcessor.getTotalTravelDistance(courierId)).thenReturn(1250.56789);

        CourierResponse response = courierQueueService.getTotalTravelDistance(courierId);

        assertNotNull(response);
        assertEquals(1250.57, response.totalMeters());
        assertEquals(1.25, response.kilometers());

        verify(commandProcessor, times(1)).getTotalTravelDistance(courierId);
    }

    // ❌ 4. Mesafe hesaplama (Başarısız - Exception)
    @Test
    void testGetTotalTravelDistance_Failure() {
        String courierId = "courier-987";
        when(commandProcessor.getTotalTravelDistance(courierId)).thenThrow(new RuntimeException("Database Error"));

        assertThrows(RuntimeException.class, () -> courierQueueService.getTotalTravelDistance(courierId));
    }

    // ✅ 5. Kuyruk işleme başlatma testi
    @Test
    void testInit_ExecutorServiceStartsSuccessfully() {
        courierQueueService.init();

        assertNotNull(courierQueueService.executorService);
    }

    // ❌ 6. Shutdown testi (Başarısız - Kesinti)
    @Test
    void testShutdown_InterruptedException() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.awaitTermination(anyLong(), any())).thenThrow(new InterruptedException());

        courierQueueService.executorService = mockExecutor;

        assertDoesNotThrow(() -> courierQueueService.shutdown());

        verify(mockExecutor, times(1)).shutdown();
        verify(mockExecutor, times(1)).shutdownNow();
    }




}
