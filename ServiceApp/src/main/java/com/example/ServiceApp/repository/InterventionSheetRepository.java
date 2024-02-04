package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.InterventionSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories

public interface InterventionSheetRepository extends JpaRepository<InterventionSheet, Long> {

    List<InterventionSheet> findBySerialNumber(String keyword);

}
