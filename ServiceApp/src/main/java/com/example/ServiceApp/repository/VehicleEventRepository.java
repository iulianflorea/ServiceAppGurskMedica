package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.VehicleEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEvent, Long> {
    List<VehicleEvent> findByVehicleId(Long vehicleId);
}
