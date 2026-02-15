package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttendanceStatusDto {
    private Boolean isCheckedIn;
    private LocalDateTime checkInTime;
    private Long attendanceId;
}
