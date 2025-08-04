package com.example.ServiceApp.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@CrossOrigin(origins = "http://188.24.7.49:4200")
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

            return ResponseEntity.ok(Map.of("message", "File uploaded successfully."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/documents")
    public ResponseEntity<List<Map<String, String>>> listDocuments(@PathVariable Long id) {
        try {
            Path interventionFolder = rootLocation.resolve("intervention-" + id);
            if (!Files.exists(interventionFolder)) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, String>> files = Files.list(interventionFolder)
                    .map(path -> {
                        Map<String, String> fileData = new HashMap<>();
                        fileData.put("name", path.getFileName().toString());
                        return fileData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}/documents/{filename:.+}")
    public ResponseEntity<Resource> getDocument(@PathVariable Long id,
                                                @PathVariable String filename) {
        try {
            Path filePath = rootLocation.resolve("intervention-" + id).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}/documents/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, @PathVariable String filename) {
        try {
            Path filePath = rootLocation.resolve("intervention-" + id).resolve(filename);
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }
}
