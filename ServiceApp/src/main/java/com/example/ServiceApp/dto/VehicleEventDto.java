package com.example.ServiceApp.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEventDto {
    private Long id;
    private Long vehicleId;
    private LocalDate date;
    private String type;
    private String description;
    private Double cost;
}
