package com.courierTrackingSystem.spring_boot.repository;

import com.courierTrackingSystem.spring_boot.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByCourierId(String courierId);
}