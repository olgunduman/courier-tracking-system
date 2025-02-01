package com.courierTrackingSystem.spring_boot.controller;

import com.courierTrackingSystem.spring_boot.dto.courier.CourierLocationDTO;
import com.courierTrackingSystem.spring_boot.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {


    private final CourierService courierService;

    /**
     * Kuryenin konumunu günceller.
     *
     * @param locationDTO CourierLocationDTO nesnesi
     * @return Başarı mesajı
     */
    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody CourierLocationDTO locationDTO) {
        courierService.enqueueLocation(locationDTO);
        return ResponseEntity.ok("Location enqueued for processing");
    }

    /**
     * Belirli bir kurye için toplam kat edilen mesafeyi döndürür.
     *
     * @param courierId Kurye ID'si
     * @return Toplam mesafe (metre cinsinden)
     */
    @GetMapping("/{courierId}/distance")
    public ResponseEntity<Double> getTotalDistance(@PathVariable String courierId) {
        Double distance = courierService.getTotalTravelDistance(courierId);
        return ResponseEntity.ok(distance);
    }
}