package com.courierTrackingSystem.spring_boot.model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courier_locations")
public class CourierLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Column(name="timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name="lat", nullable = false)
    private double lat;

    @Column(name="lng", nullable = false)
    private double lng;


    public CourierLocation(Courier courier, LocalDateTime timestamp, double lat, double lng) {
        this.courier = courier;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;

    }
}
