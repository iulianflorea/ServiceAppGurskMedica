package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttendanceReportDto {
    private Long userId;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalWorkedHours;
    private Double totalOvertimeHours;
    private Integer totalDaysWorked;
    private List<AttendanceDto> attendances;
}
