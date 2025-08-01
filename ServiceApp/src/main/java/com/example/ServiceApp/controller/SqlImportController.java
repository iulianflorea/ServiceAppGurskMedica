package com.example.ServiceApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/database")
public class SqlImportController {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sql-import")
    public ResponseEntity<String> importSqlFile(@RequestParam("file") MultipartFile file) {
        try {
            // Salvează fișierul temporar
            File tempFile = File.createTempFile("import-", ".sql");
            file.transferTo(tempFile);

            // Rulează fișierul SQL cu comanda mysql
            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql",
                    "-u", "root",
                    "-prgbiuli1",  // sau folosește `--defaults-extra-file`
                    "serviceapp"
            );
            pb.redirectInput(tempFile);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok("SQL importat cu succes.");
            } else {
                return ResponseEntity.status(500).body("Eroare la import.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Eroare: " + e.getMessage());
        }
    }

}

