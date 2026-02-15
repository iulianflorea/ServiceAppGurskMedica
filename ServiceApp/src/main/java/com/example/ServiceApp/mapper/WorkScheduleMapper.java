package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.WorkScheduleDto;
import com.example.ServiceApp.entity.WorkSchedule;
import org.springframework.stereotype.Component;

@Component
public class WorkScheduleMapper {

    public WorkScheduleDto toDto(WorkSchedule workSchedule) {
        if (workSchedule == null) {
            return null;
        }
        return WorkScheduleDto.builder()
                .id(workSchedule.getId())
                .startTime(workSchedule.getStartTime())
                .endTime(workSchedule.getEndTime())
                .breakMinutes(workSchedule.getBreakMinutes())
                .standardWorkHours(workSchedule.getStandardWorkHours())
                .workDays(workSchedule.getWorkDays())
                .weekendBonusMultiplier(workSchedule.getWeekendBonusMultiplier())
                .isActive(workSchedule.getIsActive())
                .build();
    }

    public WorkSchedule toEntity(WorkScheduleDto dto) {
        if (dto == null) {
            return null;
        }
        return WorkSchedule.builder()
                .id(dto.getId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .breakMinutes(dto.getBreakMinutes())
                .standardWorkHours(dto.getStandardWorkHours())
                .workDays(dto.getWorkDays())
                .weekendBonusMultiplier(dto.getWeekendBonusMultiplier())
                .isActive(dto.getIsActive())
                .build();
    }
}
