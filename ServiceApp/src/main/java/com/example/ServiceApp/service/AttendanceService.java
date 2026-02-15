package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.AttendanceDto;
import com.example.ServiceApp.dto.AttendanceReportDto;
import com.example.ServiceApp.dto.AttendanceStatusDto;
import com.example.ServiceApp.dto.ManualAttendanceDto;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceDto checkIn(String userEmail);

    AttendanceDto checkOut(String userEmail);

    AttendanceStatusDto getStatus(String userEmail);

    List<AttendanceDto> getMyAttendance(String userEmail);

    List<AttendanceDto> getMyAttendance(String userEmail, LocalDate startDate, LocalDate endDate);

    List<AttendanceDto> getAllAttendance();

    List<AttendanceDto> getAllAttendance(LocalDate startDate, LocalDate endDate);

    List<AttendanceDto> getAttendanceByUserId(Long userId);

    List<AttendanceDto> getAttendanceByUserId(Long userId, LocalDate startDate, LocalDate endDate);

    AttendanceDto createManualAttendance(ManualAttendanceDto manualAttendanceDto);

    AttendanceReportDto generateReport(String userEmail, LocalDate startDate, LocalDate endDate);

    AttendanceReportDto generateReportByUserId(Long userId, LocalDate startDate, LocalDate endDate);

    byte[] generatePdfReport(String userEmail, LocalDate startDate, LocalDate endDate);

    byte[] generatePdfReportByUserId(Long userId, LocalDate startDate, LocalDate endDate);

    AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto);

    void deleteAttendance(Long id);

    byte[] generateAllEmployeesExcelReport(LocalDate startDate, LocalDate endDate);
}
