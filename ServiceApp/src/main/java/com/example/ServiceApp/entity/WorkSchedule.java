package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "work_schedule")
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "break_minutes")
    private Integer breakMinutes;

    @Column(name = "standard_work_hours")
    private Double standardWorkHours;

    @Column(name = "work_days")
    private String workDays;

    @Column(name = "weekend_bonus_multiplier")
    private Double weekendBonusMultiplier;

    @Column(name = "is_active")
    private Boolean isActive;
}
