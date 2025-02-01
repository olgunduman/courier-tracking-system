package com.courier.tracking.repository;


import com.courier.tracking.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByCourierId(String courierId);
}