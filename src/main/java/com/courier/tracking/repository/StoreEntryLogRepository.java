package com.courier.tracking.repository;

import com.courier.tracking.model.StoreEntryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreEntryLogRepository extends JpaRepository<StoreEntryLog, Long> {
    Optional<StoreEntryLog> findByCourierIdAndStoreName(String courierId, String storeName);
}