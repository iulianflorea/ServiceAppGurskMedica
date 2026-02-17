package com.example.ServiceApp.controller;

import com.example.ServiceApp.config.JwtService;
import com.example.ServiceApp.dto.AttendanceDto;
import com.example.ServiceApp.dto.AttendanceReportDto;
import com.example.ServiceApp.dto.AttendanceStatusDto;
import com.example.ServiceApp.dto.ManualAttendanceDto;
import com.example.ServiceApp.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final JwtService jwtService;

    public AttendanceController(AttendanceService attendanceService, JwtService jwtService) {
        this.attendanceService = attendanceService;
        this.jwtService = jwtService;
    }

    private String extractEmailFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtService.extractUsername(token);
    }

    // ==================== USER ENDPOINTS ====================

    @PostMapping("/check-in")
    public ResponseEntity<AttendanceDto> checkIn(HttpServletRequest request) {
        String email = extractEmailFromRequest(request);
        AttendanceDto attendance = attendanceService.checkIn(email);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/check-out")
    public ResponseEntity<AttendanceDto> checkOut(HttpServletRequest request) {
        String email = extractEmailFromRequest(request);
        AttendanceDto attendance = attendanceService.checkOut(email);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/status")
    public ResponseEntity<AttendanceStatusDto> getStatus(HttpServletRequest request) {
        String email = extractEmailFromRequest(request);
        AttendanceStatusDto status = attendanceService.getStatus(email);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/my-attendance")
    public ResponseEntity<List<AttendanceDto>> getMyAttendance(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String email = extractEmailFromRequest(request);
        List<AttendanceDto> attendances;
        if (startDate != null && endDate != null) {
            attendances = attendanceService.getMyAttendance(email, startDate, endDate);
        } else {
            attendances = attendanceService.getMyAttendance(email);
        }
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/report")
    public ResponseEntity<AttendanceReportDto> getMyReport(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String email = extractEmailFromRequest(request);
        AttendanceReportDto report = attendanceService.generateReport(email, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> getMyPdfReport(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String email = extractEmailFromRequest(request);
        byte[] pdfBytes = attendanceService.generatePdfReport(email, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "pontaj_" + startDate + "_" + endDate + ".txt");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/manual")
    public ResponseEntity<AttendanceDto> createMyManualAttendance(
            HttpServletRequest request,
            @RequestBody ManualAttendanceDto manualAttendanceDto) {
        String email = extractEmailFromRequest(request);
        AttendanceDto attendance = attendanceService.createMyManualAttendance(email, manualAttendanceDto);
        return ResponseEntity.ok(attendance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDto> updateMyAttendance(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody AttendanceDto attendanceDto) {
        String email = extractEmailFromRequest(request);
        AttendanceDto updated = attendanceService.updateMyAttendance(email, id, attendanceDto);
        return ResponseEntity.ok(updated);
    }

    // ==================== ADMIN ENDPOINTS ====================

    @GetMapping("/admin/all")
    public ResponseEntity<List<AttendanceDto>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto> attendances;
        if (startDate != null && endDate != null) {
            attendances = attendanceService.getAllAttendance(startDate, endDate);
        } else {
            attendances = attendanceService.getAllAttendance();
        }
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto> attendances;
        if (startDate != null && endDate != null) {
            attendances = attendanceService.getAttendanceByUserId(userId, startDate, endDate);
        } else {
            attendances = attendanceService.getAttendanceByUserId(userId);
        }
        return ResponseEntity.ok(attendances);
    }

    @PostMapping("/admin/manual")
    public ResponseEntity<AttendanceDto> createManualAttendance(@RequestBody ManualAttendanceDto manualAttendanceDto) {
        AttendanceDto attendance = attendanceService.createManualAttendance(manualAttendanceDto);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/admin/report/{userId}")
    public ResponseEntity<AttendanceReportDto> getReportByUserId(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AttendanceReportDto report = attendanceService.generateReportByUserId(userId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/admin/report/pdf/{userId}")
    public ResponseEntity<byte[]> getPdfReportByUserId(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdfBytes = attendanceService.generatePdfReportByUserId(userId, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "pontaj_user_" + userId + "_" + startDate + "_" + endDate + ".txt");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/admin/report/excel/all")
    public ResponseEntity<byte[]> getAllEmployeesExcelReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] excelBytes = attendanceService.generateAllEmployeesExcelReport(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "raport_ore_suplimentare_" + startDate + "_" + endDate + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<AttendanceDto> updateAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceDto attendanceDto) {
        AttendanceDto updated = attendanceService.updateAttendance(id, attendanceDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
