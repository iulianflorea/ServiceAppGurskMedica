package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.VehicleInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleInsuranceRepository extends JpaRepository<VehicleInsurance, Long> {
    List<VehicleInsurance> findByVehicleId(Long vehicleId);

    @Query("SELECT ins FROM VehicleInsurance ins WHERE ins.expiryDate BETWEEN :from AND :to")
    List<VehicleInsurance> findByExpiryDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
