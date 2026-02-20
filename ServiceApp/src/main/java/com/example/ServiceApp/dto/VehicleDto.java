package com.example.ServiceApp.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
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
    private String notes;
    private String photoName;
    private Long userId;
    private String userName;

    private List<VehicleRevisionDto> revisions;
    private List<VehicleItpDto> itpList;
    private List<VehicleInsuranceDto> insuranceList;
    private List<VehicleEventDto> events;
}
