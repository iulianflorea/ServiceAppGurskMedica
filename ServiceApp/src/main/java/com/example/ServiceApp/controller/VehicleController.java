package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.*;
import com.example.ServiceApp.service.VehicleAlertTask;
import com.example.ServiceApp.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleAlertTask vehicleAlertTask;
    private final ObjectMapper objectMapper;

    // ── Vehicle CRUD ────────────────────────────────────────────────

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<VehicleDto> create(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        VehicleDto dto = objectMapper.readValue(dataJson, VehicleDto.class);
        return ResponseEntity.ok(vehicleService.create(dto, photo));
    }

    @GetMapping
    public ResponseEntity<List<VehicleDto>> findAll() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<VehicleDto> update(
            @PathVariable Long id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        VehicleDto dto = objectMapper.readValue(dataJson, VehicleDto.class);
        return ResponseEntity.ok(vehicleService.update(id, dto, photo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehicleDto>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(vehicleService.search(keyword));
    }

    /**
     * Endpoint de test — declanșează imediat verificarea expirărilor și trimiterea emailurilor.
     * Folosit pentru a testa că SMTP-ul funcționează fără a aștepta cron-ul de la 08:00.
     * Exemplu: POST /api/vehicles/alerts/trigger
     */
    @PostMapping("/alerts/trigger")
    public ResponseEntity<String> triggerAlerts() {
        vehicleAlertTask.checkExpirations();
        return ResponseEntity.ok("Verificare expirări declanșată. Verifică log-urile serverului.");
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) throws IOException {
        vehicleService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }

    // ── Revisions ───────────────────────────────────────────────────

    @PostMapping("/{vehicleId}/revisions")
    public ResponseEntity<VehicleRevisionDto> addRevision(
            @PathVariable Long vehicleId,
            @RequestBody VehicleRevisionDto dto) {
        return ResponseEntity.ok(vehicleService.addRevision(vehicleId, dto));
    }

    @PutMapping("/revisions/{id}")
    public ResponseEntity<VehicleRevisionDto> updateRevision(
            @PathVariable Long id,
            @RequestBody VehicleRevisionDto dto) {
        return ResponseEntity.ok(vehicleService.updateRevision(id, dto));
    }

    @DeleteMapping("/revisions/{id}")
    public ResponseEntity<Void> deleteRevision(@PathVariable Long id) {
        vehicleService.deleteRevision(id);
        return ResponseEntity.noContent().build();
    }

    // ── ITP ─────────────────────────────────────────────────────────

    @PostMapping("/{vehicleId}/itp")
    public ResponseEntity<VehicleItpDto> addItp(
            @PathVariable Long vehicleId,
            @RequestBody VehicleItpDto dto) {
        return ResponseEntity.ok(vehicleService.addItp(vehicleId, dto));
    }

    @PutMapping("/itp/{id}")
    public ResponseEntity<VehicleItpDto> updateItp(
            @PathVariable Long id,
            @RequestBody VehicleItpDto dto) {
        return ResponseEntity.ok(vehicleService.updateItp(id, dto));
    }

    @DeleteMapping("/itp/{id}")
    public ResponseEntity<Void> deleteItp(@PathVariable Long id) {
        vehicleService.deleteItp(id);
        return ResponseEntity.noContent().build();
    }

    // ── Insurance ───────────────────────────────────────────────────

    @PostMapping("/{vehicleId}/insurance")
    public ResponseEntity<VehicleInsuranceDto> addInsurance(
            @PathVariable Long vehicleId,
            @RequestBody VehicleInsuranceDto dto) {
        return ResponseEntity.ok(vehicleService.addInsurance(vehicleId, dto));
    }

    @PutMapping("/insurance/{id}")
    public ResponseEntity<VehicleInsuranceDto> updateInsurance(
            @PathVariable Long id,
            @RequestBody VehicleInsuranceDto dto) {
        return ResponseEntity.ok(vehicleService.updateInsurance(id, dto));
    }

    @DeleteMapping("/insurance/{id}")
    public ResponseEntity<Void> deleteInsurance(@PathVariable Long id) {
        vehicleService.deleteInsurance(id);
        return ResponseEntity.noContent().build();
    }

    // ── Events ──────────────────────────────────────────────────────

    @PostMapping("/{vehicleId}/events")
    public ResponseEntity<VehicleEventDto> addEvent(
            @PathVariable Long vehicleId,
            @RequestBody VehicleEventDto dto) {
        return ResponseEntity.ok(vehicleService.addEvent(vehicleId, dto));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<VehicleEventDto> updateEvent(
            @PathVariable Long id,
            @RequestBody VehicleEventDto dto) {
        return ResponseEntity.ok(vehicleService.updateEvent(id, dto));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        vehicleService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
