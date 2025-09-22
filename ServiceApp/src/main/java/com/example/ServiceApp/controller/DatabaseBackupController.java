package com.example.ServiceApp.controller;

import com.example.ServiceApp.service.BackupService;
import com.example.ServiceApp.service.DatabaseBackupTask;
import com.example.ServiceApp.service.UploadsBackupTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class DatabaseBackupController {

    private final DatabaseBackupTask dbBackupTask;
    private final BackupService backupService;
    private final UploadsBackupTask uploadsBackupTask;

    public DatabaseBackupController(DatabaseBackupTask dbBackupTask, BackupService backupService, UploadsBackupTask uploadsBackupTask) {
        this.dbBackupTask = dbBackupTask;
        this.backupService = backupService;
        this.uploadsBackupTask = uploadsBackupTask;
    }

    @PostMapping("/database")
    public ResponseEntity<String> manualDatabaseBackup() {
        dbBackupTask.runBackup();
        return ResponseEntity.ok("Backup baza de date pornit manual.");
    }

    @PostMapping("/manual")
    public ResponseEntity<String> manualBackup() {
        uploadsBackupTask.backupUploads();
        return ResponseEntity.ok("Backup completed manually.");
    }
}
