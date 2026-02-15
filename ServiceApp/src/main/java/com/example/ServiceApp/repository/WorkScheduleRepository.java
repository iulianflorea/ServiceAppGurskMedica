package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    Optional<WorkSchedule> findByIsActiveTrue();
}
