export interface AttendanceDto {
  id?: number;
  userId?: number;
  userFirstname?: string;
  userLastname?: string;
  userEmail?: string;
  date?: string;
  checkInTime?: string;
  checkOutTime?: string;
  workedHours?: number;
  overtimeHours?: number;
  notes?: string;
  isManual?: boolean;
}

export interface AttendanceStatusDto {
  isCheckedIn?: boolean;
  checkInTime?: string;
  attendanceId?: number;
}

export interface ManualAttendanceDto {
  userId?: number;
  date?: string;
  checkInTime?: string;
  checkOutTime?: string;
  notes?: string;
}

export interface WorkScheduleDto {
  id?: number;
  startTime?: string;
  endTime?: string;
  breakMinutes?: number;
  standardWorkHours?: number;
  workDays?: string;
  weekendBonusMultiplier?: number;
  isActive?: boolean;
}

export interface AttendanceReportDto {
  userId?: number;
  userFirstname?: string;
  userLastname?: string;
  userEmail?: string;
  startDate?: string;
  endDate?: string;
  totalWorkedHours?: number;
  totalOvertimeHours?: number;
  totalDaysWorked?: number;
  attendances?: AttendanceDto[];
}
