package com.example.ServiceApp.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleInsuranceDto {
    private Long id;
    private Long vehicleId;
    private LocalDate date;
    private Integer validityMonths;
    private LocalDate expiryDate;
    private String insurer;
    private String policyNumber;
    private Double cost;
}
