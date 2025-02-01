package com.courierTrackingSystem.spring_boot.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "couriers")
public class Courier {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="courier_id", nullable = false, unique = true)
    private String courierId;

    // Bir kurye birden fazla konum güncellemesine sahip olabilir
    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourierLocation> locations;


    public Courier(String courierId) {
        this.courierId = courierId;
    }
}
