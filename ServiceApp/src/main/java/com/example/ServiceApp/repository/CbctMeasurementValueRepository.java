package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.CbctMeasurementValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CbctMeasurementValueRepository extends JpaRepository<CbctMeasurementValue, Long> {
    List<CbctMeasurementValue> findByMeasurementId(Long measurementId);
    void deleteByMeasurementId(Long measurementId);
}
