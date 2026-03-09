package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.CbctDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CbctDeviceRepository extends JpaRepository<CbctDevice, Long> {
    List<CbctDevice> findAllByOrderByBrandAscModelAsc();
}
