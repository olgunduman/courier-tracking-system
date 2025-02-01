package com.courierTrackingSystem.spring_boot.repository;

import com.courierTrackingSystem.spring_boot.model.StoreEntryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreEntryLogRepository extends JpaRepository<StoreEntryLog, Long> {
    Optional<StoreEntryLog> findByCourierIdAndStoreName(String courierId, String storeName);
}