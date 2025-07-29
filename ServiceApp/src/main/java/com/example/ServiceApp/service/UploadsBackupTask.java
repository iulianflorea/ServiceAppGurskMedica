package com.example.ServiceApp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UploadsBackupTask {

    private final Path sourceFolder = Paths.get("uploads");
    private final Path backupBaseFolder = Paths.get("D:/ServiceApp-backup");

    // Rulează în fiecare zi la 2:00 dimineața
    @Scheduled(cron = "0 0 2 * * *")
    public void backupUploads() {
        try {
            if (!Files.exists(sourceFolder)) {
                System.out.println("Folderul uploads nu există.");
                return;
            }

            // Creează folderul de backup dacă nu există
            Files.createDirectories(backupBaseFolder);

            // Nume cu dată: backups/2025-07-29
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            Path todayBackup = backupBaseFolder.resolve(today);
            Files.createDirectories(todayBackup);

            // Copiază tot conținutul
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

        } catch (IOException e) {
            System.err.println("Eroare la backup: " + e.getMessage());
        }
    }
}
