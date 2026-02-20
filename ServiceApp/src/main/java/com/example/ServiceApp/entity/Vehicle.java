package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;
    private String vin;
    private String make;
    private String model;
    private Integer year;
    private String color;
    private String fuelType;
    private Integer engineCapacity;
    private Integer power;
    private Integer currentKm;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String photoName;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
