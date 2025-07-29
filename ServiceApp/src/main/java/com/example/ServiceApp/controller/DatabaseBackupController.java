package com.example.ServiceApp.controller;

import com.example.ServiceApp.service.DatabaseBackupTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class DatabaseBackupController {

    private final DatabaseBackupTask dbBackupTask;

    public DatabaseBackupController(DatabaseBackupTask dbBackupTask) {
        this.dbBackupTask = dbBackupTask;
    }

    @PostMapping("/database")
    public ResponseEntity<String> manualDatabaseBackup() {
        dbBackupTask.runBackup();
        return ResponseEntity.ok("Backup baza de date pornit manual.");
    }
}
