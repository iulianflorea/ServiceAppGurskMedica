package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.BackupDto;
import com.example.ServiceApp.dto.CustomerDto;
import com.example.ServiceApp.entity.Backup;
import com.example.ServiceApp.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BackupMapper {

    public Backup toBackup(BackupDto backupDto) {
        return Backup.builder()
                .sqlPath(backupDto.getSqlPath())
                .documentPath(backupDto.getDocumentPath())
                .build();
    }

    public BackupDto toDto(Backup backup) {
        return BackupDto.builder()
                .id(backup.getId())
                .sqlPath(backup.getSqlPath())
                .documentPath(backup.getDocumentPath())
                .build();
    }

    public List<BackupDto> toDtoList(List<Backup> backupList) {
        List<BackupDto> backupDtoList = new ArrayList<>();
        for (Backup backup : backupList) {
            BackupDto backupDto = toDto(backup);
            backupDtoList.add(backupDto);
        }
        return backupDtoList;
    }
}
