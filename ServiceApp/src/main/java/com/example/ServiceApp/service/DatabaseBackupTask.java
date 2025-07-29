package com.example.ServiceApp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DatabaseBackupTask {

    private final String dbUser = "root";
    private final String dbPassword = "rgbiuli1";
    private final String dbName = "serviceapp";
    private final String backupFolder = "D:/backup-mysql";

    @Scheduled(cron = "0 30 2 * * ?") // backup zilnic la 02:30 AM
    public void scheduledDatabaseBackup() {
        runBackup();
    }

    public void runBackup() {
        try {
            Files.createDirectories(Paths.get(backupFolder));
            String backupFileName = String.format("backup-%s.sql",
                    new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date()));
            String backupPath = backupFolder + "/" + backupFileName;

            String mysqldumpPath = "\"C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe\""; // adaptează dacă e nevoie

            String command = String.format(
                    "%s -u%s -p%s %s -r \"%s\"",
                    mysqldumpPath, dbUser, dbPassword, dbName, backupPath
            );

            System.out.println(backupPath);

            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Backup MySQL realizat cu succes.");
            } else {
                System.err.println("Backup MySQL a eșuat cu cod: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
