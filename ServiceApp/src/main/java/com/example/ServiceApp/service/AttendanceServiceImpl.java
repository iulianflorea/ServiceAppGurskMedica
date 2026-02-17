package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.AttendanceDto;
import com.example.ServiceApp.dto.AttendanceReportDto;
import com.example.ServiceApp.dto.AttendanceStatusDto;
import com.example.ServiceApp.dto.ManualAttendanceDto;
import com.example.ServiceApp.dto.WorkScheduleDto;
import com.example.ServiceApp.entity.Attendance;
import com.example.ServiceApp.entity.User;
import com.example.ServiceApp.entity.WorkSchedule;
import com.example.ServiceApp.mapper.AttendanceMapper;
import com.example.ServiceApp.repository.AttendanceRepository;
import com.example.ServiceApp.repository.UserRepository;
import com.example.ServiceApp.repository.WorkScheduleRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final AttendanceMapper attendanceMapper;
    private final WorkScheduleService workScheduleService;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                  UserRepository userRepository,
                                  WorkScheduleRepository workScheduleRepository,
                                  AttendanceMapper attendanceMapper,
                                  WorkScheduleService workScheduleService) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.workScheduleRepository = workScheduleRepository;
        this.attendanceMapper = attendanceMapper;
        this.workScheduleService = workScheduleService;
    }

    @Override
    public AttendanceDto checkIn(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserAndDate(user, today);

        if (existingAttendance.isPresent() && existingAttendance.get().getCheckOutTime() == null) {
            throw new RuntimeException("Already checked in today");
        }

        Attendance attendance = Attendance.builder()
                .user(user)
                .date(today)
                .checkInTime(LocalDateTime.now())
                .isManual(false)
                .build();

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    public AttendanceDto checkOut(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findOpenAttendanceByUserAndDate(user, today)
                .orElseThrow(() -> new RuntimeException("No active check-in found for today"));

        attendance.setCheckOutTime(LocalDateTime.now());
        calculateHours(attendance);

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    public AttendanceStatusDto getStatus(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        Optional<Attendance> openAttendance = attendanceRepository.findOpenAttendanceByUserAndDate(user, today);

        if (openAttendance.isPresent()) {
            Attendance attendance = openAttendance.get();
            return AttendanceStatusDto.builder()
                    .isCheckedIn(true)
                    .checkInTime(attendance.getCheckInTime())
                    .attendanceId(attendance.getId())
                    .build();
        }

        return AttendanceStatusDto.builder()
                .isCheckedIn(false)
                .checkInTime(null)
                .attendanceId(null)
                .build();
    }

    @Override
    public List<AttendanceDto> getMyAttendance(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Attendance> attendances = attendanceRepository.findByUserOrderByDateDesc(user);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    public List<AttendanceDto> getMyAttendance(String userEmail, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Attendance> attendances = attendanceRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    public List<AttendanceDto> getAllAttendance() {
        List<Attendance> attendances = attendanceRepository.findAllByOrderByDateDesc();
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    public List<AttendanceDto> getAllAttendance(LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    public List<AttendanceDto> getAttendanceByUserId(Long userId) {
        List<Attendance> attendances = attendanceRepository.findByUserIdOrderByDateDesc(userId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    public List<AttendanceDto> getAttendanceByUserId(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    public AttendanceDto createManualAttendance(ManualAttendanceDto manualAttendanceDto) {
        User user = userRepository.findById(manualAttendanceDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Attendance attendance = Attendance.builder()
                .user(user)
                .date(manualAttendanceDto.getDate())
                .checkInTime(manualAttendanceDto.getCheckInTime())
                .checkOutTime(manualAttendanceDto.getCheckOutTime())
                .notes(manualAttendanceDto.getNotes())
                .isManual(true)
                .build();

        if (attendance.getCheckOutTime() != null) {
            calculateHours(attendance);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    public AttendanceReportDto generateReport(String userEmail, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildReport(user, startDate, endDate);
    }

    @Override
    public AttendanceReportDto generateReportByUserId(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildReport(user, startDate, endDate);
    }

    private AttendanceReportDto buildReport(User user, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        List<AttendanceDto> attendanceDtos = attendanceMapper.toDtoList(attendances);

        double totalWorked = attendances.stream()
                .filter(a -> a.getWorkedHours() != null)
                .mapToDouble(Attendance::getWorkedHours)
                .sum();

        double totalOvertime = attendances.stream()
                .filter(a -> a.getOvertimeHours() != null)
                .mapToDouble(Attendance::getOvertimeHours)
                .sum();

        // Count unique days (not number of records)
        int daysWorked = (int) attendances.stream()
                .filter(a -> a.getCheckOutTime() != null)
                .map(Attendance::getDate)
                .distinct()
                .count();

        return AttendanceReportDto.builder()
                .userId(user.getId())
                .userFirstname(user.getFirstname())
                .userLastname(user.getLastname())
                .userEmail(user.getEmail())
                .startDate(startDate)
                .endDate(endDate)
                .totalWorkedHours(Math.round(totalWorked * 100.0) / 100.0)
                .totalOvertimeHours(Math.round(totalOvertime * 100.0) / 100.0)
                .totalDaysWorked(daysWorked)
                .attendances(attendanceDtos)
                .build();
    }

    @Override
    public byte[] generatePdfReport(String userEmail, LocalDate startDate, LocalDate endDate) {
        AttendanceReportDto report = generateReport(userEmail, startDate, endDate);
        return generatePdfFromReport(report);
    }

    @Override
    public byte[] generatePdfReportByUserId(Long userId, LocalDate startDate, LocalDate endDate) {
        AttendanceReportDto report = generateReportByUserId(userId, startDate, endDate);
        return generatePdfFromReport(report);
    }

    private byte[] generatePdfFromReport(AttendanceReportDto report) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        writer.println("RAPORT PONTAJ");
        writer.println("=".repeat(60));
        writer.println();
        writer.printf("Angajat: %s %s%n", report.getUserFirstname(), report.getUserLastname());
        writer.printf("Email: %s%n", report.getUserEmail());
        writer.printf("Perioada: %s - %s%n", report.getStartDate().format(dateFormatter), report.getEndDate().format(dateFormatter));
        writer.println();
        writer.println("-".repeat(60));
        writer.println();
        writer.printf("Total zile lucrate: %d%n", report.getTotalDaysWorked());
        writer.printf("Total ore lucrate: %s%n", formatHoursToTime(report.getTotalWorkedHours()));
        writer.printf("Total ore suplimentare: %s%n", formatHoursToTime(report.getTotalOvertimeHours()));
        writer.println();
        writer.println("-".repeat(60));
        writer.println("DETALII PONTAJ:");
        writer.println("-".repeat(60));
        writer.printf("%-12s %-8s %-8s %-10s %-10s %-8s%n", "Data", "Intrare", "Iesire", "Lucrate", "Suplim.", "Manual");
        writer.println("-".repeat(60));

        for (AttendanceDto attendance : report.getAttendances()) {
            String checkIn = attendance.getCheckInTime() != null ? attendance.getCheckInTime().format(timeFormatter) : "-";
            String checkOut = attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().format(timeFormatter) : "-";
            String worked = formatHoursToTime(attendance.getWorkedHours());
            String overtime = formatHoursToTime(attendance.getOvertimeHours());
            String manual = Boolean.TRUE.equals(attendance.getIsManual()) ? "Da" : "Nu";

            writer.printf("%-12s %-8s %-8s %-10s %-10s %-8s%n",
                    attendance.getDate().format(dateFormatter),
                    checkIn, checkOut, worked, overtime, manual);
        }

        writer.println("-".repeat(60));
        writer.println();
        writer.printf("Generat la: %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        writer.flush();
        writer.close();

        return baos.toByteArray();
    }

    private void calculateHours(Attendance attendance) {
        if (attendance.getCheckInTime() == null || attendance.getCheckOutTime() == null) {
            return;
        }

        WorkScheduleDto schedule = workScheduleService.getActiveSchedule();

        Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
        double hoursWorked = duration.toMinutes() / 60.0;

        // Subtract break time if worked more than 4 hours
        int breakMinutes = schedule.getBreakMinutes() != null ? schedule.getBreakMinutes() : 0;
        if (hoursWorked > 4 && breakMinutes > 0) {
            hoursWorked -= breakMinutes / 60.0;
        }

        // Round to 2 decimal places
        hoursWorked = Math.round(hoursWorked * 100.0) / 100.0;
        attendance.setWorkedHours(hoursWorked);

        // Calculate overtime
        double standardHours = schedule.getStandardWorkHours() != null ? schedule.getStandardWorkHours() : 8.0;
        double overtime = 0.0;

        DayOfWeek dayOfWeek = attendance.getDate().getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

        if (isWeekend) {
            // All weekend hours count as overtime (multiplier applied only for payment calculation)
            overtime = hoursWorked;
        } else if (hoursWorked > standardHours) {
            // Weekday overtime = hours worked beyond standard hours
            overtime = hoursWorked - standardHours;
        }

        overtime = Math.round(overtime * 100.0) / 100.0;
        attendance.setOvertimeHours(overtime);
    }

    @Override
    public AttendanceDto createMyManualAttendance(String userEmail, ManualAttendanceDto manualAttendanceDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Attendance attendance = Attendance.builder()
                .user(user)
                .date(manualAttendanceDto.getDate())
                .checkInTime(manualAttendanceDto.getCheckInTime())
                .checkOutTime(manualAttendanceDto.getCheckOutTime())
                .notes(manualAttendanceDto.getNotes())
                .isManual(true)
                .build();

        if (attendance.getCheckOutTime() != null) {
            calculateHours(attendance);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    public AttendanceDto updateMyAttendance(String userEmail, Long id, AttendanceDto attendanceDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        if (!attendance.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Nu aveți permisiunea să editați acest pontaj");
        }

        if (attendanceDto.getDate() != null) {
            attendance.setDate(attendanceDto.getDate());
        }
        if (attendanceDto.getCheckInTime() != null) {
            attendance.setCheckInTime(attendanceDto.getCheckInTime());
        }
        if (attendanceDto.getCheckOutTime() != null) {
            attendance.setCheckOutTime(attendanceDto.getCheckOutTime());
        }
        if (attendanceDto.getNotes() != null) {
            attendance.setNotes(attendanceDto.getNotes());
        }

        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            calculateHours(attendance);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    public AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        // Update date if provided
        if (attendanceDto.getDate() != null) {
            attendance.setDate(attendanceDto.getDate());
        }

        // Update check-in time if provided
        if (attendanceDto.getCheckInTime() != null) {
            attendance.setCheckInTime(attendanceDto.getCheckInTime());
        }

        // Update check-out time if provided
        if (attendanceDto.getCheckOutTime() != null) {
            attendance.setCheckOutTime(attendanceDto.getCheckOutTime());
        }

        // Update notes if provided
        if (attendanceDto.getNotes() != null) {
            attendance.setNotes(attendanceDto.getNotes());
        }

        // Recalculate hours
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            calculateHours(attendance);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        attendanceRepository.delete(attendance);
    }

    private String formatHoursToTime(Double decimalHours) {
        if (decimalHours == null) {
            return "-";
        }
        int hours = (int) Math.floor(decimalHours);
        int minutes = (int) Math.round((decimalHours - hours) * 60);
        return String.format("%d:%02d", hours, minutes);
    }

    @Override
    public byte[] generateAllEmployeesExcelReport(LocalDate startDate, LocalDate endDate) {
        List<User> allUsers = userRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Raport Ore Suplimentare");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("RAPORT ORE SUPLIMENTARE - " + startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    " - " + endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Create header row
            Row headerRow = sheet.createRow(2);
            String[] headers = {"Nr.", "Nume", "Prenume", "Email", "Zile Lucrate", "Ore Lucrate", "Ore Suplimentare"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 3;
            int nr = 1;
            double grandTotalWorked = 0;
            double grandTotalOvertime = 0;
            int grandTotalDays = 0;

            for (User user : allUsers) {
                List<Attendance> attendances = attendanceRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);

                if (attendances.isEmpty()) {
                    continue;
                }

                double totalWorked = attendances.stream()
                        .filter(a -> a.getWorkedHours() != null)
                        .mapToDouble(Attendance::getWorkedHours)
                        .sum();

                double totalOvertime = attendances.stream()
                        .filter(a -> a.getOvertimeHours() != null)
                        .mapToDouble(Attendance::getOvertimeHours)
                        .sum();

                int daysWorked = (int) attendances.stream()
                        .filter(a -> a.getCheckOutTime() != null)
                        .map(Attendance::getDate)
                        .distinct()
                        .count();

                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(nr++);
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(user.getLastname() != null ? user.getLastname() : "");
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(user.getFirstname() != null ? user.getFirstname() : "");
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(user.getEmail() != null ? user.getEmail() : "");
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(daysWorked);
                cell4.setCellStyle(dataStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(formatHoursToTime(totalWorked));
                cell5.setCellStyle(dataStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(formatHoursToTime(totalOvertime));
                cell6.setCellStyle(dataStyle);

                grandTotalWorked += totalWorked;
                grandTotalOvertime += totalOvertime;
                grandTotalDays += daysWorked;
            }

            // Add totals row
            rowNum++;
            Row totalRow = sheet.createRow(rowNum);

            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderBottom(BorderStyle.THICK);
            totalStyle.setBorderTop(BorderStyle.THICK);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);
            totalStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(totalStyle);

            for (int i = 1; i <= 3; i++) {
                Cell emptyCell = totalRow.createCell(i);
                emptyCell.setCellStyle(totalStyle);
            }

            Cell totalDaysCell = totalRow.createCell(4);
            totalDaysCell.setCellValue(grandTotalDays);
            totalDaysCell.setCellStyle(totalStyle);

            Cell totalWorkedCell = totalRow.createCell(5);
            totalWorkedCell.setCellValue(formatHoursToTime(grandTotalWorked));
            totalWorkedCell.setCellStyle(totalStyle);

            Cell totalOvertimeCell = totalRow.createCell(6);
            totalOvertimeCell.setCellValue(formatHoursToTime(grandTotalOvertime));
            totalOvertimeCell.setCellStyle(totalStyle);

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel report", e);
        }
    }
}
