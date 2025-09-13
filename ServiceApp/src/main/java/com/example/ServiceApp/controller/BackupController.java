package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.BackupDto;
import com.example.ServiceApp.dto.EmployeeDto;
import com.example.ServiceApp.entity.Backup;
import com.example.ServiceApp.service.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/admin/set-path")
    public BackupDto create(@RequestBody BackupDto backupDto) {
        return backupService.create(backupDto);

    }

    @GetMapping("/admin/get-paths")
    public List<BackupDto> findAll() {
        return backupService.findAll();
    }

    @GetMapping("/admin/findById/{id}")
    public BackupDto findById(@PathVariable Long id) {
        return backupService.findById(id);
    }

    @PutMapping()
    public BackupDto update(@RequestBody BackupDto backupDto) {
        return backupService.update(backupDto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        backupService.delete(id);
    }

}