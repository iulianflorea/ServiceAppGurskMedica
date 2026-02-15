package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ManualAttendanceDto {
    private Long userId;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String notes;
}
