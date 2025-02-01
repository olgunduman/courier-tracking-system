package com.courier.tracking.service;


import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.dto.CourierResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.*;

import java.util.stream.IntStream;

import static com.courier.tracking.utils.CalculationUtils.roundToTwoDecimalPlaces;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourierQueueService {


    public BlockingQueue<CourierLocationRequest> locationQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    public ExecutorService executorService;
    private final CourierLocationCommandProcessor commandProcessor;



    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(4);
        IntStream.range(0, 4).forEach(i -> executorService.submit(this::processQueue));
        log.info("CourierQueueService initialized with 4 worker threads.");
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down CourierQueueService...");
        executorService.shutdown();
        try {
            Optional.of(executorService.awaitTermination(60, TimeUnit.SECONDS))
                    .filter(terminated -> !terminated)
                    .ifPresent(terminated -> executorService.shutdownNow());
        } catch (InterruptedException e) {
            log.error("Interrupted during shutdown.", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    public void enqueueLocation(CourierLocationRequest request) {
        try {
            locationQueue.put(request);
            log.debug("Enqueued location update for courier: {}", request.getCourierId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to enqueue location update.", e);
        }
    }


    public CourierResponse getTotalTravelDistance(String courierId) {
        double totalTravelDistance = commandProcessor.getTotalTravelDistance(courierId);

        return CourierResponse.builder()
                .totalMeters(roundToTwoDecimalPlaces(totalTravelDistance))
                .kilometers(roundToTwoDecimalPlaces(totalTravelDistance / 1000))
                .build();
    }

    private void processQueue() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                CourierLocationRequest courierLocationRequest = locationQueue.poll(1, TimeUnit.SECONDS);
                if (courierLocationRequest != null) {
                    commandProcessor.processLocationCommand(courierLocationRequest);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Worker thread interrupted, exiting queue processing loop.");
                scheduler.shutdown();
            } catch (Exception ex) {
                log.error("Error processing location update command.", ex);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}