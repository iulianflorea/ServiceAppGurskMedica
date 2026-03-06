import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AttendanceDto,
  AttendanceStatusDto,
  ManualAttendanceDto,
  WorkScheduleDto,
  AttendanceReportDto
} from '../dtos/attendanceDto';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  private apiUrl = `${environment.apiUrl}/api/attendance`;
  private workScheduleUrl = `${environment.apiUrl}/api/work-schedule`;

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  // ==================== USER ENDPOINTS ====================

  checkIn(): Observable<AttendanceDto> {
    return this.http.post<AttendanceDto>(`${this.apiUrl}/check-in`, {}, {
      headers: this.getAuthHeaders()
    });
  }

  checkOut(): Observable<AttendanceDto> {
    return this.http.post<AttendanceDto>(`${this.apiUrl}/check-out`, {}, {
      headers: this.getAuthHeaders()
    });
  }

  getStatus(): Observable<AttendanceStatusDto> {
    return this.http.get<AttendanceStatusDto>(`${this.apiUrl}/status`, {
      headers: this.getAuthHeaders()
    });
  }

  getMyAttendance(startDate?: string, endDate?: string): Observable<AttendanceDto[]> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    return this.http.get<AttendanceDto[]>(`${this.apiUrl}/my-attendance`, {
      headers: this.getAuthHeaders(),
      params
    });
  }

  getMyReport(startDate: string, endDate: string): Observable<AttendanceReportDto> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<AttendanceReportDto>(`${this.apiUrl}/report`, {
      headers: this.getAuthHeaders(),
      params
    });
  }

  downloadMyPdfReport(startDate: string, endDate: string): Observable<Blob> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get(`${this.apiUrl}/report/pdf`, {
      headers: this.getAuthHeaders(),
      params,
      responseType: 'blob'
    });
  }

  createMyManualAttendance(dto: ManualAttendanceDto): Observable<AttendanceDto> {
    return this.http.post<AttendanceDto>(`${this.apiUrl}/manual`, dto, {
      headers: this.getAuthHeaders()
    });
  }

  updateMyAttendance(id: number, dto: AttendanceDto): Observable<AttendanceDto> {
    return this.http.put<AttendanceDto>(`${this.apiUrl}/${id}`, dto, {
      headers: this.getAuthHeaders()
    });
  }

  // ==================== ADMIN ENDPOINTS ====================

  getAllAttendance(startDate?: string, endDate?: string): Observable<AttendanceDto[]> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    return this.http.get<AttendanceDto[]>(`${this.apiUrl}/admin/all`, {
      headers: this.getAuthHeaders(),
      params
    });
  }

  getAttendanceByUserId(userId: number, startDate?: string, endDate?: string): Observable<AttendanceDto[]> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    return this.http.get<AttendanceDto[]>(`${this.apiUrl}/admin/user/${userId}`, {
      headers: this.getAuthHeaders(),
      params
    });
  }

  createManualAttendance(dto: ManualAttendanceDto): Observable<AttendanceDto> {
    return this.http.post<AttendanceDto>(`${this.apiUrl}/admin/manual`, dto, {
      headers: this.getAuthHeaders()
    });
  }

  getReportByUserId(userId: number, startDate: string, endDate: string): Observable<AttendanceReportDto> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<AttendanceReportDto>(`${this.apiUrl}/admin/report/${userId}`, {
      headers: this.getAuthHeaders(),
      params
    });
  }

  downloadPdfReportByUserId(userId: number, startDate: string, endDate: string): Observable<Blob> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get(`${this.apiUrl}/admin/report/pdf/${userId}`, {
      headers: this.getAuthHeaders(),
      params,
      responseType: 'blob'
    });
  }

  downloadAllEmployeesExcelReport(startDate: string, endDate: string): Observable<Blob> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get(`${this.apiUrl}/admin/report/excel/all`, {
      headers: this.getAuthHeaders(),
      params,
      responseType: 'blob'
    });
  }

  updateAttendance(id: number, dto: AttendanceDto): Observable<AttendanceDto> {
    return this.http.put<AttendanceDto>(`${this.apiUrl}/admin/${id}`, dto, {
      headers: this.getAuthHeaders()
    });
  }

  deleteAttendance(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/admin/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  // ==================== WORK SCHEDULE ====================

  getWorkSchedule(): Observable<WorkScheduleDto> {
    return this.http.get<WorkScheduleDto>(this.workScheduleUrl, {
      headers: this.getAuthHeaders()
    });
  }

  updateWorkSchedule(dto: WorkScheduleDto): Observable<WorkScheduleDto> {
    return this.http.put<WorkScheduleDto>(this.workScheduleUrl, dto, {
      headers: this.getAuthHeaders()
    });
  }
}
