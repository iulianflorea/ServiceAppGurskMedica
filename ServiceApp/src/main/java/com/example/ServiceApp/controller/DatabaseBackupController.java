package com.example.ServiceApp.controller;

import com.example.ServiceApp.service.BackupService;
import com.example.ServiceApp.service.DatabaseBackupTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class DatabaseBackupController {

    private final DatabaseBackupTask dbBackupTask;
    private final BackupService backupService;

    public DatabaseBackupController(DatabaseBackupTask dbBackupTask, BackupService backupService) {
        this.dbBackupTask = dbBackupTask;
        this.backupService = backupService;
    }

    @PostMapping("/database")
    public ResponseEntity<String> manualDatabaseBackup() {
        dbBackupTask.runBackup();
        return ResponseEntity.ok("Backup baza de date pornit manual.");
    }

    @PostMapping("/manual")
    public ResponseEntity<String> manualBackup() {
        backupService.doBackup();
        return ResponseEntity.ok("Backup completed manually.");
    }
}
