package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.BackupDto;
import com.example.ServiceApp.entity.Backup;
import com.example.ServiceApp.mapper.BackupMapper;
import com.example.ServiceApp.repository.BackupRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BackupService {

    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;

    public BackupService(BackupRepository backupRepository, BackupMapper backupMapper) {
        this.backupRepository = backupRepository;
        this.backupMapper = backupMapper;
    }


    public BackupDto create(BackupDto backupDto) {
        Backup backupToBeSaved = backupMapper.toBackup(backupDto);
        if (backupDto.getId() == null) {
            Backup backupSaved = backupRepository.save(backupToBeSaved);
            return backupMapper.toDto(backupSaved);
        } else {
            update(backupDto);
        }
        return backupMapper.toDto(backupToBeSaved);
    }

    public BackupDto findById(Long id) {
        Backup backup = backupRepository.findById(id).orElseThrow();
        return backupMapper.toDto(backup);
    }

    public List<BackupDto> findAll() {
        List<Backup> backupList = backupRepository.findAll();
        return backupMapper.toDtoList(backupList);
    }

    public BackupDto update(BackupDto backupDto) {
        Backup backupToBeUpdated = backupRepository.findById(1L).orElseThrow();
        backupToBeUpdated.setSqlPath(backupDto.getSqlPath());
        backupToBeUpdated.setDocumentPath(backupDto.getDocumentPath());
        Backup backupUpdated = backupRepository.save(backupToBeUpdated);
        return backupMapper.toDto(backupUpdated);
    }

    public void delete(Long id) {
        backupRepository.deleteById(id);
    }

    public Optional<String> getBackupPath() {
        return backupRepository.findAll()
                .stream()
                .findFirst()
                .map(Backup::getDocumentPath);
    }

    public Optional<String> getSqlPath() {
        return backupRepository.findAll()
                .stream()
                .findFirst()
                .map(Backup::getSqlPath);

    }

}
