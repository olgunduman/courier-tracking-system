package com.courier.tracking.repository;

import com.courier.tracking.model.CourierLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {
}