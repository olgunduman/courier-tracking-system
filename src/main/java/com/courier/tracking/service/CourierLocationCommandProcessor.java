package com.courier.tracking.service;

import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.model.Courier;
import com.courier.tracking.dto.Store;
import com.courier.tracking.model.StoreEntryLog;
import com.courier.tracking.repository.CourierLocationRepository;
import com.courier.tracking.repository.CourierRepository;
import com.courier.tracking.repository.StoreEntryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.courier.tracking.mapper.StoreEntryLogMapper.getEntryLog;
import static com.courier.tracking.utils.CalculationUtils.calculateDistance;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourierLocationCommandProcessor {


    private final StoreService storeService;
    private final StoreEntryLogRepository storeEntryLogRepository;
    private final CourierRepository courierRepository;
    private final CourierLocationRepository courierLocationRepository;

    @Value("${store.entry.distance}")
    private double storeEntryDistance;

    private final ConcurrentHashMap<String, Double> totalDistances = new ConcurrentHashMap<>();

    public void processLocationCommand(CourierLocationRequest locationDTO) {
        Courier courier = courierRepository.findByCourierId(locationDTO.getCourierId()).orElseGet(() -> {
            Courier newCourierDto = Courier.builder().courierId(locationDTO.getCourierId()).locations(new ArrayList<>()).build();
            return courierRepository.save(newCourierDto);
        });

        if (!courier.getLocations().isEmpty()) {
            double lastLat = courier.getLocations().get(courier.getLocations().size() - 1).getLat();
            double lastLng = courier.getLocations().get(courier.getLocations().size() - 1).getLng();
            double distance = calculateDistance(lastLat, lastLng, locationDTO.getLat(), locationDTO.getLng());
            courier.setTotalDistance(courier.getTotalDistance() + distance);
            totalDistances.merge(locationDTO.getCourierId(), distance, Double::sum);
        }

        CourierLocationRequest courierLocation = CourierLocationRequest.builder().courierId(courier.getCourierId()).timestamp(locationDTO.getTimestamp()).lat(locationDTO.getLat()).lng(locationDTO.getLng()).build();

        courierLocationRepository.save(courierLocation.toEntity(courier));

        log.info("Saved new location for courier: {}", courier.getCourierId());

        List<Store> stores = storeService.getStores();
        for (Store store : stores) {
            double distanceToStore = calculateDistance(locationDTO.getLat(), locationDTO.getLng(), store.lat(), store.lng());

            if (distanceToStore <= storeEntryDistance) {
                logStoreEntry(courier, store.name(), locationDTO.getTimestamp());
            }
        }

    }

    public Double getTotalTravelDistance(String courierId) {
        return totalDistances.getOrDefault(courierId, 0.0);
    }

    private void logStoreEntry(Courier courier, String storeName, LocalDateTime timestamp) {
        Optional<StoreEntryLog> optionalLog = storeEntryLogRepository.findByCourierIdAndStoreName(courier.getCourierId(), storeName);

        if (optionalLog.isPresent()) {
            StoreEntryLog storeEntryLog = optionalLog.get();
            Duration elapsed = Duration.between(storeEntryLog.getLastEntryTime(), timestamp);
            if (elapsed.toMinutes() >= 1) {
                log.info("Courier {} entered {} at {}", courier.getCourierId(), storeName, timestamp);
                storeEntryLog.setLastEntryTime(timestamp);
                storeEntryLogRepository.save(storeEntryLog);
            } else {
                log.info("Courier {} re-entered {} within 1 minute. Skipping log.", courier.getCourierId(), storeName);
            }
        } else {
            log.info("Courier {} entered {} at {} (first entry)", courier.getCourierId(), storeName, timestamp);
            StoreEntryLog newLog = getEntryLog(courier, storeName, timestamp);
            storeEntryLogRepository.save(newLog);
        }
    }


}
