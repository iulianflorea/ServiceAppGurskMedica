package com.example.ServiceApp.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRevisionDto {
    private Long id;
    private Long vehicleId;
    private LocalDate date;
    private Integer km;
    private Double cost;
    private String description;
}
