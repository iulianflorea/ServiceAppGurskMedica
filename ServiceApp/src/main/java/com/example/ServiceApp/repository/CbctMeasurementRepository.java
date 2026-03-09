package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.CbctMeasurement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CbctMeasurementRepository extends JpaRepository<CbctMeasurement, Long> {

    List<CbctMeasurement> findAllByOrderByMeasurementDateDescIdDesc(Pageable pageable);

    List<CbctMeasurement> findAllByOrderByMeasurementDateDescIdDesc();

    @Query("""
        SELECT m FROM CbctMeasurement m
        LEFT JOIN m.customer c
        LEFT JOIN m.device d
        WHERE (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(COALESCE(c.name, ''))         LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(d.brand, ''))         LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(d.model, ''))         LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(m.serialNumber, ''))  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            CAST(m.measurementDate AS string)    LIKE CONCAT('%', :keyword, '%')
        )
        ORDER BY m.measurementDate DESC, m.id DESC
    """)
    List<CbctMeasurement> search(@Param("keyword") String keyword);
}
