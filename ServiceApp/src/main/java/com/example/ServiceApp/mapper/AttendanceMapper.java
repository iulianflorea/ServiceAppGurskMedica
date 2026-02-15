package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.AttendanceDto;
import com.example.ServiceApp.entity.Attendance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttendanceMapper {

    public AttendanceDto toDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }
        return AttendanceDto.builder()
                .id(attendance.getId())
                .userId(attendance.getUser() != null ? attendance.getUser().getId() : null)
                .userFirstname(attendance.getUser() != null ? attendance.getUser().getFirstname() : null)
                .userLastname(attendance.getUser() != null ? attendance.getUser().getLastname() : null)
                .userEmail(attendance.getUser() != null ? attendance.getUser().getEmail() : null)
                .date(attendance.getDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .workedHours(attendance.getWorkedHours())
                .overtimeHours(attendance.getOvertimeHours())
                .notes(attendance.getNotes())
                .isManual(attendance.getIsManual())
                .build();
    }

    public List<AttendanceDto> toDtoList(List<Attendance> attendances) {
        List<AttendanceDto> dtoList = new ArrayList<>();
        for (Attendance attendance : attendances) {
            dtoList.add(toDto(attendance));
        }
        return dtoList;
    }
}
