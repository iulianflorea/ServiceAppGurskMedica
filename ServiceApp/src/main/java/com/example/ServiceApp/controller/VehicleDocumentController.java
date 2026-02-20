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
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleDocumentController {

    private final Path rootLocation = Paths.get("uploads");

    @PostMapping("/{id}/documents")
    public ResponseEntity<?> uploadDocument(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file) {
        try {
            Path vehicleDocsFolder = rootLocation.resolve("vehicle-" + id).resolve("docs");
            Files.createDirectories(vehicleDocsFolder);

            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path destinationFile = vehicleDocsFolder.resolve(filename);
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
            Path vehicleDocsFolder = rootLocation.resolve("vehicle-" + id).resolve("docs");
            if (!Files.exists(vehicleDocsFolder)) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, String>> files = Files.list(vehicleDocsFolder)
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
            Path vehicleDocsFolder = rootLocation.resolve("vehicle-" + id).resolve("docs");
            Path filePath = vehicleDocsFolder.resolve(filename).normalize();

            if (!filePath.startsWith(vehicleDocsFolder)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

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
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}/documents/{filename:.+}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id, @PathVariable String filename) {
        try {
            Path filePath = rootLocation.resolve("vehicle-" + id).resolve("docs").resolve(filename);
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id,
                                             @PathVariable(required = false) String filename) {
        try {
            Path photoDir = rootLocation.resolve("vehicle-" + id).resolve("photo");
            if (!Files.exists(photoDir)) {
                return ResponseEntity.notFound().build();
            }
            Optional<Path> photoFile = Files.list(photoDir).findFirst();
            if (photoFile.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Path filePath = photoFile.get();
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
