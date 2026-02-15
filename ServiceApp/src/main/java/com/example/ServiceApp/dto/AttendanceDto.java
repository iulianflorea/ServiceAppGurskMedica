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
public class AttendanceDto {
    private Long id;
    private Long userId;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double workedHours;
    private Double overtimeHours;
    private String notes;
    private Boolean isManual;
}
