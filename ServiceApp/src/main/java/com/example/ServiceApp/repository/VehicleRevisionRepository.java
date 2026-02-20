package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.VehicleRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRevisionRepository extends JpaRepository<VehicleRevision, Long> {
    List<VehicleRevision> findByVehicleId(Long vehicleId);
}
