package com.example.ServiceApp.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;

@Service
public class BackupService {

    private final Path uploadsFolder = Paths.get("C:/Users/iulian.florea/OneDrive/Desktop/uploads/");
    private final Path backupBaseFolder = Paths.get("D:/ServiceApp-backup");

    public void doBackup() {
        String date = LocalDate.now().toString();
        Path destination = backupBaseFolder.resolve("backup-" + date);

        try {
            Files.walkFileTree(uploadsFolder, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = destination.resolve(uploadsFolder.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = destination.resolve(uploadsFolder.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });

            System.out.println("Backup successful to: " + destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
