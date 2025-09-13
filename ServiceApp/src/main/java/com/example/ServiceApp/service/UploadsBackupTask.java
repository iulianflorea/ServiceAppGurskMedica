package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.BackupDto;
import com.example.ServiceApp.entity.Backup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UploadsBackupTask {

    private final Path sourceFolder = Paths.get("uploads");
    private final BackupService backupService;

    public UploadsBackupTask(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void backupUploads() {
        backupService.getBackupPath().ifPresentOrElse(backupPathStr -> {
            Path backupBaseFolder = Paths.get(backupPathStr);

            try {
                if (!Files.exists(sourceFolder)) {
                    System.out.println("Folderul uploads nu există.");
                    return;
                }

                Files.createDirectories(backupBaseFolder);

                String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                Path todayBackup = backupBaseFolder.resolve(today);
                Files.createDirectories(todayBackup);

                Files.walk(sourceFolder)
                        .filter(Files::isRegularFile)
                        .forEach(sourcePath -> {
                            try {
                                Path relativePath = sourceFolder.relativize(sourcePath);
                                Path destinationPath = todayBackup.resolve(relativePath);
                                Files.createDirectories(destinationPath.getParent());
                                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.err.println("Eroare la copierea fișierului: " + sourcePath);
                            }
                        });

                System.out.println("Backup creat cu succes în " + todayBackup);

                cleanupKeepLastTwo(backupBaseFolder);

            } catch (IOException e) {
                System.err.println("Eroare la backup: " + e.getMessage());
            }
        }, () -> {
            System.out.println("Nu a fost setată nicio cale de backup!");
        });
    }

    private void cleanupKeepLastTwo(Path backupBaseFolder) {
        try (Stream<Path> paths = Files.list(backupBaseFolder)) {
            List<Path> sortedBackups = paths
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .collect(Collectors.toList());

            if (sortedBackups.size() > 2) {
                List<Path> toDelete = sortedBackups.subList(2, sortedBackups.size());
                for (Path folder : toDelete) {
                    deleteDirectoryRecursively(folder);
                    System.out.println("Șters backup vechi: " + folder);
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la ștergerea backupurilor vechi: " + e.getMessage());
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                System.err.println("Nu am putut șterge: " + p);
                            }
                        });
            }
        }
    }
}
