import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatNativeDateModule } from '@angular/material/core';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { AttendanceService } from '../services/attendance.service';
import { AttendanceDto, AttendanceReportDto } from '../dtos/attendanceDto';
import { UserDto } from '../dtos/userDto';
import { ManualAttendanceDialogComponent } from '../manual-attendance-dialog/manual-attendance-dialog.component';
import { EditAttendanceDialogComponent } from '../edit-attendance-dialog/edit-attendance-dialog.component';
import { PasswordResetDialogComponent } from '../password-reset-dialog/password-reset-dialog.component';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-attendance-list',
  templateUrl: './attendance-list.component.html',
  styleUrls: ['./attendance-list.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatCardModule,
    MatDialogModule,
    MatTooltipModule,
    MatNativeDateModule,
    RouterModule
  ]
})
export class AttendanceListComponent implements OnInit {

  displayedColumns: string[] = ['date', 'userFullName', 'checkInTime', 'checkOutTime', 'workedHours', 'overtimeHours', 'isManual', 'actions'];
  dataSource = new MatTableDataSource<AttendanceDto>([]);

  users: UserDto[] = [];
  selectedUserId: number | null = null;
  startDate: Date | null = null;
  endDate: Date | null = null;
  isAdmin = false;
  isMobile = false;

  report: AttendanceReportDto | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private attendanceService: AttendanceService,
    private httpClient: HttpClient,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.checkMobile();
    window.addEventListener('resize', () => this.checkMobile());
    this.checkUserRole();
    this.loadAttendance();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  checkMobile() {
    this.isMobile = window.innerWidth < 768;
  }

  checkUserRole() {
    this.httpClient.get(`${environment.apiUrl}/user/current-role`, { responseType: 'text' }).subscribe({
      next: (role: string) => {
        this.isAdmin = role === 'ADMIN';
        if (this.isAdmin) {
          this.loadUsers();
        }
      },
      error: (err) => console.error('Error getting user role:', err)
    });
  }

  loadUsers() {
    this.httpClient.get<UserDto[]>(`${environment.apiUrl}/user/findAll`).subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (err) => console.error('Error loading users:', err)
    });
  }

  loadAttendance() {
    const startDateStr = this.startDate ? this.formatDate(this.startDate) : undefined;
    const endDateStr = this.endDate ? this.formatDate(this.endDate) : undefined;

    if (this.isAdmin && this.selectedUserId) {
      this.attendanceService.getAttendanceByUserId(this.selectedUserId, startDateStr, endDateStr).subscribe({
        next: (data) => {
          this.dataSource.data = data;
          this.loadReport();
        },
        error: (err) => console.error('Error loading attendance:', err)
      });
    } else if (this.isAdmin && !this.selectedUserId) {
      this.attendanceService.getAllAttendance(startDateStr, endDateStr).subscribe({
        next: (data) => {
          this.dataSource.data = data;
          this.report = null;
        },
        error: (err) => console.error('Error loading attendance:', err)
      });
    } else {
      this.attendanceService.getMyAttendance(startDateStr, endDateStr).subscribe({
        next: (data) => {
          this.dataSource.data = data;
          this.loadReport();
        },
        error: (err) => console.error('Error loading attendance:', err)
      });
    }
  }

  loadReport() {
    if (!this.startDate || !this.endDate) {
      this.report = null;
      return;
    }

    const startDateStr = this.formatDate(this.startDate);
    const endDateStr = this.formatDate(this.endDate);

    if (this.isAdmin && this.selectedUserId) {
      this.attendanceService.getReportByUserId(this.selectedUserId, startDateStr, endDateStr).subscribe({
        next: (report) => this.report = report,
        error: (err) => console.error('Error loading report:', err)
      });
    } else if (!this.isAdmin) {
      this.attendanceService.getMyReport(startDateStr, endDateStr).subscribe({
        next: (report) => this.report = report,
        error: (err) => console.error('Error loading report:', err)
      });
    }
  }

  onFilterChange() {
    this.loadAttendance();
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatTime(dateTimeStr: string | undefined): string {
    if (!dateTimeStr) return '-';
    const date = new Date(dateTimeStr);
    return date.toLocaleTimeString('ro-RO', { hour: '2-digit', minute: '2-digit' });
  }

  formatDisplayDate(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleDateString('ro-RO');
  }

  getUserFullName(attendance: AttendanceDto): string {
    return `${attendance.userFirstname || ''} ${attendance.userLastname || ''}`.trim() || attendance.userEmail || '-';
  }

  formatHoursToTime(decimalHours: number | undefined): string {
    if (decimalHours === undefined || decimalHours === null) return '-';

    const hours = Math.floor(decimalHours);
    const minutes = Math.round((decimalHours - hours) * 60);

    return `${hours}:${String(minutes).padStart(2, '0')}`;
  }

  openManualAttendanceDialog() {
    const dialogRef = this.dialog.open(ManualAttendanceDialogComponent, {
      width: this.isMobile ? '95vw' : '500px',
      data: { users: this.users }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadAttendance();
      }
    });
  }

  exportPdf() {
    if (!this.startDate || !this.endDate) {
      alert('Selectați perioada pentru export');
      return;
    }

    const startDateStr = this.formatDate(this.startDate);
    const endDateStr = this.formatDate(this.endDate);

    if (this.isAdmin && this.selectedUserId) {
      this.attendanceService.downloadPdfReportByUserId(this.selectedUserId, startDateStr, endDateStr).subscribe({
        next: (blob) => this.downloadBlob(blob, `pontaj_${startDateStr}_${endDateStr}.txt`),
        error: (err) => console.error('Error downloading PDF:', err)
      });
    } else {
      this.attendanceService.downloadMyPdfReport(startDateStr, endDateStr).subscribe({
        next: (blob) => this.downloadBlob(blob, `pontaj_${startDateStr}_${endDateStr}.txt`),
        error: (err) => console.error('Error downloading PDF:', err)
      });
    }
  }

  exportAllEmployeesExcel() {
    if (!this.startDate || !this.endDate) {
      alert('Selectați perioada pentru export');
      return;
    }

    const startDateStr = this.formatDate(this.startDate);
    const endDateStr = this.formatDate(this.endDate);

    this.attendanceService.downloadAllEmployeesExcelReport(startDateStr, endDateStr).subscribe({
      next: (blob) => this.downloadBlob(blob, `raport_ore_suplimentare_${startDateStr}_${endDateStr}.xlsx`),
      error: (err) => {
        console.error('Error downloading Excel:', err);
        alert('Eroare la exportul Excel');
      }
    });
  }

  private downloadBlob(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  setThisMonth() {
    const now = new Date();
    this.startDate = new Date(now.getFullYear(), now.getMonth(), 1);
    this.endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    this.onFilterChange();
  }

  setLastMonth() {
    const now = new Date();
    this.startDate = new Date(now.getFullYear(), now.getMonth() - 1, 1);
    this.endDate = new Date(now.getFullYear(), now.getMonth(), 0);
    this.onFilterChange();
  }

  clearFilters() {
    this.startDate = null;
    this.endDate = null;
    this.selectedUserId = null;
    this.report = null;
    this.loadAttendance();
  }

  editAttendance(attendance: AttendanceDto) {
    const dialogRef = this.dialog.open(EditAttendanceDialogComponent, {
      width: this.isMobile ? '95vw' : '500px',
      data: { attendance }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadAttendance();
      }
    });
  }

  deleteAttendance(attendance: AttendanceDto) {
    if (!attendance.id) return;

    const userName = `${attendance.userFirstname || ''} ${attendance.userLastname || ''}`.trim();
    const dateStr = this.formatDisplayDate(attendance.date);

    if (confirm(`Sigur doriți să ștergeți pontajul lui ${userName} din ${dateStr}?`)) {
      this.attendanceService.deleteAttendance(attendance.id).subscribe({
        next: () => {
          this.loadAttendance();
        },
        error: (err) => {
          console.error('Error deleting attendance:', err);
          alert('Eroare la ștergerea pontajului');
        }
      });
    }
  }

  openPasswordResetDialog() {
    this.dialog.open(PasswordResetDialogComponent, {
      width: this.isMobile ? '95vw' : '400px',
      data: { users: this.users }
    });
  }
}
