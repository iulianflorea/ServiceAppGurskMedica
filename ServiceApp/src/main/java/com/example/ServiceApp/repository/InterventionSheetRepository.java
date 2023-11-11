package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.InterventionSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterventionSheetRepository extends JpaRepository<InterventionSheet, Long> {
}
