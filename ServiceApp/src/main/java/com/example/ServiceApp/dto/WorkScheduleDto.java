package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkScheduleDto {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakMinutes;
    private Double standardWorkHours;
    private String workDays;
    private Double weekendBonusMultiplier;
    private Boolean isActive;
}
