package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.Backup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long> {
}
