package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.WorkScheduleDto;
import com.example.ServiceApp.service.WorkScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-schedule")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @GetMapping
    public ResponseEntity<WorkScheduleDto> getActiveSchedule() {
        WorkScheduleDto schedule = workScheduleService.getActiveSchedule();
        return ResponseEntity.ok(schedule);
    }

    @PutMapping
    public ResponseEntity<WorkScheduleDto> updateSchedule(@RequestBody WorkScheduleDto workScheduleDto) {
        WorkScheduleDto updated = workScheduleService.updateSchedule(workScheduleDto);
        return ResponseEntity.ok(updated);
    }
}
