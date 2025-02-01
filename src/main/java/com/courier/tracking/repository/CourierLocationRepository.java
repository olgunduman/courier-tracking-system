package com.courierTrackingSystem.spring_boot.repository;

import com.courierTrackingSystem.spring_boot.model.Courier;
import com.courierTrackingSystem.spring_boot.model.CourierLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {
    List<CourierLocation> findByCourierOrderByTimestampAsc(Courier courier);
}