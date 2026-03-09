package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.CbctDeviceDto;
import com.example.ServiceApp.dto.CbctMeasurementDto;
import com.example.ServiceApp.service.CbctService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cbct")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CbctController {

    private final CbctService cbctService;

    // ── Devices ──────────────────────────────────────────────────────────────

    @GetMapping("/devices")
    public List<CbctDeviceDto> getAllDevices() {
        return cbctService.findAllDevices();
    }

    @GetMapping("/devices/{id}")
    public CbctDeviceDto getDevice(@PathVariable Long id) {
        return cbctService.findDeviceById(id);
    }

    @PostMapping("/devices")
    public CbctDeviceDto saveDevice(@RequestBody CbctDeviceDto dto) {
        return cbctService.saveDevice(dto);
    }

    @DeleteMapping("/devices/{id}")
    public void deleteDevice(@PathVariable Long id) {
        cbctService.deleteDevice(id);
    }

    // ── Measurements ─────────────────────────────────────────────────────────

    @GetMapping("/measurements")
    public List<CbctMeasurementDto> getMeasurements(@RequestParam(required = false, defaultValue = "") String keyword) {
        if (keyword.isBlank()) return cbctService.findAllMeasurements();
        return cbctService.searchMeasurements(keyword);
    }

    @GetMapping("/measurements/{id}")
    public CbctMeasurementDto getMeasurement(@PathVariable Long id) {
        return cbctService.findMeasurementById(id);
    }

    @PostMapping("/measurements")
    public CbctMeasurementDto saveMeasurement(@RequestBody CbctMeasurementDto dto) {
        return cbctService.saveMeasurement(dto);
    }

    @DeleteMapping("/measurements/{id}")
    public void deleteMeasurement(@PathVariable Long id) {
        cbctService.deleteMeasurement(id);
    }

    @GetMapping("/measurements/export")
    public ResponseEntity<byte[]> exportExcel() throws Exception {
        return cbctService.exportExcel();
    }

    @GetMapping("/measurements/{id}/excel")
    public ResponseEntity<byte[]> exportMeasurementExcel(@PathVariable Long id) throws Exception {
        return cbctService.exportMeasurementExcel(id);
    }

    @GetMapping("/measurements/{id}/pdf")
    public ResponseEntity<byte[]> exportMeasurementPdf(@PathVariable Long id) throws Exception {
        return cbctService.exportMeasurementPdf(id);
    }
}
