package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.VehicleItp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleItpRepository extends JpaRepository<VehicleItp, Long> {
    List<VehicleItp> findByVehicleId(Long vehicleId);

    @Query("SELECT i FROM VehicleItp i WHERE i.expiryDate BETWEEN :from AND :to")
    List<VehicleItp> findByExpiryDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
