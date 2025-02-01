package com.courier.tracking.controller;


import com.courier.tracking.dto.CourierLocationRequest;
import com.courier.tracking.dto.CourierResponse;
import com.courier.tracking.service.CourierQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
@Validated

public class CourierController {

    private final CourierQueueService courierQueueService;

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody @Valid CourierLocationRequest request) {
        courierQueueService.enqueueLocation(request);
        return ResponseEntity.ok("Location updated successfully.");
    }

    @GetMapping("/{courierId}/distance")
    public CourierResponse getTotalDistance(@PathVariable String courierId) {
        return courierQueueService.getTotalTravelDistance(courierId);
    }
}