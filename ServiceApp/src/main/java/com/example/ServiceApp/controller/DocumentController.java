package com.example.ServiceApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/interventions")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {


    private final Path rootLocation = Paths.get("uploads");

    @PostMapping("/{id}/documents")
    public ResponseEntity<?> uploadDocument(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file) {
        try {
            // Creează folderul dacă nu există
            Path interventionFolder = rootLocation.resolve("intervention-" + id);
            Files.createDirectories(interventionFolder);

            // Salvează fișierul
            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path destinationFile = interventionFolder.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/documents")
    public ResponseEntity<List<Map<String, String>>> listDocuments(@PathVariable Long id) {
        try {
            Path folder = rootLocation.resolve("intervention-" + id);
            if (!Files.exists(folder)) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, String>> files = Files.list(folder)
                    .map(path -> {
                        Map<String, String> fileInfo = new HashMap<>();
                        fileInfo.put("name", path.getFileName().toString());
                        fileInfo.put("url", "/api/files/intervention-" + id + "/" + path.getFileName());
                        return fileInfo;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}/documents/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, @PathVariable String filename) {
        try {
            Path filePath = rootLocation.resolve("intervention-" + id).resolve(filename);
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok("File deleted");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }





}
