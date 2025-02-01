package com.courierTrackingSystem.spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store_entry_logs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"courier_id", "store_name"})
})
public class StoreEntryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false)
    private String courierId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "last_entry_time", nullable = false)
    private LocalDateTime lastEntryTime;

    public StoreEntryLog(String courierId, String storeName, LocalDateTime timestamp) {
        this.courierId = courierId;
        this.storeName = storeName;
        this.lastEntryTime = timestamp;
    }
}