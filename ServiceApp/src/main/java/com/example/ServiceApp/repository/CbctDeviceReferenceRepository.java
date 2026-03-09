package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.CbctDeviceReference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CbctDeviceReferenceRepository extends JpaRepository<CbctDeviceReference, Long> {
    List<CbctDeviceReference> findByDeviceId(Long deviceId);
    void deleteByDeviceId(Long deviceId);
}
