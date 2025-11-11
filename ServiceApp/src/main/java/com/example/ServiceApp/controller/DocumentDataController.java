package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.DocumentDataDto;
import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.DocumentData;
import com.example.ServiceApp.repository.DocumentDataRepository;
import com.example.ServiceApp.service.DocumentDataService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/documents")
public class DocumentDataController {
    private final DocumentDataService documentDataService;
    private DocumentDataRepository repo;

    public DocumentDataController(DocumentDataService documentDataService) {
        this.documentDataService = documentDataService;
    }

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<DocumentDataDto> create(@RequestBody DocumentDataDto dto) {
        DocumentDataDto created = documentDataService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update")
    public DocumentDataDto update(@PathVariable Long id, DocumentDataDto dto) {
        return documentDataService.update(id, dto);
    }

    @GetMapping("/get-all")
    public List<DocumentDataDto> getAll() {
        return documentDataService.getAll();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDataDto>> search(@RequestParam String keyword) {
        List<DocumentDataDto> documentDataDtoList = documentDataService.search(keyword);
        return new ResponseEntity<>(documentDataDtoList, HttpStatus.OK);
    }

    @GetMapping("/get-by-id/{id}")
    public DocumentDataDto getById(@PathVariable Long id) {
        return documentDataService.getById(id);
    }

    @GetMapping("/export/{id}/{type}")
    public ResponseEntity<byte[]> exportDocument(@PathVariable Long id, @PathVariable String type) throws Exception {
        DocumentDataDto data = documentDataService.getById(id);
        ByteArrayOutputStream out = documentDataService.generateDocument(data, type);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "_" + id + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        documentDataService.delete(id);
    }
}
