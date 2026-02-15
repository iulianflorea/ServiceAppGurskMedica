package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.WorkScheduleDto;
import com.example.ServiceApp.entity.WorkSchedule;
import com.example.ServiceApp.mapper.WorkScheduleMapper;
import com.example.ServiceApp.repository.WorkScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleServiceImpl(WorkScheduleRepository workScheduleRepository, WorkScheduleMapper workScheduleMapper) {
        this.workScheduleRepository = workScheduleRepository;
        this.workScheduleMapper = workScheduleMapper;
    }

    @Override
    public WorkScheduleDto getActiveSchedule() {
        return workScheduleRepository.findByIsActiveTrue()
                .map(workScheduleMapper::toDto)
                .orElseGet(this::createDefaultSchedule);
    }

    @Override
    public WorkScheduleDto updateSchedule(WorkScheduleDto workScheduleDto) {
        WorkSchedule existingSchedule = workScheduleRepository.findByIsActiveTrue()
                .orElse(null);

        if (existingSchedule != null) {
            existingSchedule.setStartTime(workScheduleDto.getStartTime());
            existingSchedule.setEndTime(workScheduleDto.getEndTime());
            existingSchedule.setBreakMinutes(workScheduleDto.getBreakMinutes());
            existingSchedule.setStandardWorkHours(workScheduleDto.getStandardWorkHours());
            existingSchedule.setWorkDays(workScheduleDto.getWorkDays());
            existingSchedule.setWeekendBonusMultiplier(workScheduleDto.getWeekendBonusMultiplier());
            WorkSchedule saved = workScheduleRepository.save(existingSchedule);
            return workScheduleMapper.toDto(saved);
        } else {
            WorkSchedule newSchedule = workScheduleMapper.toEntity(workScheduleDto);
            newSchedule.setIsActive(true);
            WorkSchedule saved = workScheduleRepository.save(newSchedule);
            return workScheduleMapper.toDto(saved);
        }
    }

    @Override
    public WorkScheduleDto createDefaultSchedule() {
        WorkSchedule defaultSchedule = WorkSchedule.builder()
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(18, 0))
                .breakMinutes(30)
                .standardWorkHours(8.0)
                .workDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
                .weekendBonusMultiplier(1.5)
                .isActive(true)
                .build();
        WorkSchedule saved = workScheduleRepository.save(defaultSchedule);
        return workScheduleMapper.toDto(saved);
    }
}
