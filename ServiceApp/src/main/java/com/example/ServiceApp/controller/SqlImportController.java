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
//VARIANTA PENTRU WINDOWS
//
//@RestController
//@RequestMapping("/database")
//public class SqlImportController {
//
//    @PostMapping("/sql-import")
//    public ResponseEntity<String> importSqlFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // Salvează fișierul temporar
//            File tempFile = File.createTempFile("import-", ".sql");
//            file.transferTo(tempFile);
//
//            // Rulează fișierul SQL cu comanda mysql
//            ProcessBuilder pb = new ProcessBuilder(
//                    "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql",
//                    "-u", "root",
//                    "-prgbiuli1",  // sau folosește `--defaults-extra-file`
//                    "serviceapp"
//            );
//            pb.redirectInput(tempFile);
//            Process process = pb.start();
//
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                return ResponseEntity.ok("SQL importat cu succes.");
//            } else {
//                return ResponseEntity.status(500).body("Eroare la import.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Eroare: " + e.getMessage());
//        }
//    }
//
//}

//VARIANTA PENTRU LINUX UBUNTU

@RestController
@RequestMapping("/database")
public class SqlImportController {

    @PostMapping("/sql-import")
    public ResponseEntity<String> importSqlFile(@RequestParam("file") MultipartFile file) {
        try {
            // Salvează fișierul temporar
            File tempFile = File.createTempFile("import-", ".sql");
            file.transferTo(tempFile);

            // Comanda Linux pentru import SQL
            String command = String.format(
                    "mysql -u%s -p%s %s < %s",
                    "root",
                    "rgbiuli1",
                    "serviceapp",
                    tempFile.getAbsolutePath()
            );

            // Rulăm comanda prin shell (pentru redirecționare <)
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return ResponseEntity.ok("SQL importat cu succes.");
            } else {
                return ResponseEntity.status(500).body("Eroare la import. Cod: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Eroare: " + e.getMessage());
        }
    }

}

