package com.example.ServiceApp.controller;

import com.example.ServiceApp.service.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping
    public ResponseEntity<String> manualBackup() {
        backupService.doBackup();
        return ResponseEntity.ok("Backup completed manually.");
    }
}