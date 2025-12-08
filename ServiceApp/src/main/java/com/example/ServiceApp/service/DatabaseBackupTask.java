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
//    private final String backupFolder = "";

    private final BackupService backupService;

    public DatabaseBackupTask(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "0 30 2 * * ?") // backup zilnic la 02:30 AM
    public void scheduledDatabaseBackup() {
        runBackup();
    }
//VARIANTA PENTRU WINDOWS
//
//    public void runBackup() {
//        try {
//            Files.createDirectories(Paths.get(backupService.getSqlPath().orElseThrow()));
//            String backupFileName = String.format("backup-%s.sql",
//                    new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date()));
//            String backupPath = backupService.getSqlPath().orElseThrow() + "/" + backupFileName;
//
//            String mysqldumpPath = "\"C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe\""; // adaptează dacă e nevoie
//
//            String command = String.format(
//                    "%s -u%s -p%s %s -r \"%s\"",
//                    mysqldumpPath, dbUser, dbPassword, dbName, backupPath
//            );
//
//            System.out.println(backupPath);
//
//            Process process = Runtime.getRuntime().exec(command);
//            int exitCode = process.waitFor();
//
//            if (exitCode == 0) {
//                System.out.println("Backup MySQL realizat cu succes.");
//            } else {
//                System.err.println("Backup MySQL a eșuat cu cod: " + exitCode);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    VARIANTA PENTRU LINUX UBUNTU
public void runBackup() {
    try {
        Files.createDirectories(Paths.get(backupService.getSqlPath().orElseThrow()));
        String backupFileName = String.format("backup-%s.sql",
                new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date()));
        String backupPath = backupService.getSqlPath().orElseThrow() + "/" + backupFileName;

        // Pe Linux folosim doar "mysqldump" daca este instalat in PATH
        String[] command = {
                "mysqldump",
                "-u" + dbUser,
                "-p" + dbPassword,  // IMPORTANT: fără spațiu
                dbName,
                "-r", backupPath
        };

        System.out.println("Saving backup to: " + backupPath);

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

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
