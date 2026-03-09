package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.CbctDozimetrie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CbctDozimetrieRepository extends JpaRepository<CbctDozimetrie, Long> {
    List<CbctDozimetrie> findByMeasurementId(Long measurementId);
    void deleteByMeasurementId(Long measurementId);
}
