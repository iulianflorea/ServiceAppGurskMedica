package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.WorkScheduleDto;

public interface WorkScheduleService {

    WorkScheduleDto getActiveSchedule();

    WorkScheduleDto updateSchedule(WorkScheduleDto workScheduleDto);

    WorkScheduleDto createDefaultSchedule();
}
