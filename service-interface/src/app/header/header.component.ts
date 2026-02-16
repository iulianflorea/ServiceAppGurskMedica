import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable} from "rxjs";
import {AttendanceService} from "../services/attendance.service";
import {AttendanceStatusDto} from "../dtos/attendanceDto";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  isDarkMode = true;
  isCheckedIn = false;
  checkInTime: string | null = null;
  isLoading = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private attendanceService: AttendanceService
  ) {}
  logout() {
    localStorage.clear();
    this.router.navigate(["/login"]);
  }

  isMobile: boolean = false;

  ngOnInit() {
    this.checkMobile();
    window.addEventListener('resize', this.checkMobile.bind(this));

    const savedTheme = localStorage.getItem('darkMode');
    // Default to dark mode if no preference saved
    this.isDarkMode = savedTheme === null ? true : savedTheme === 'true';
    this.applyTheme();

    this.loadAttendanceStatus();
  }

  loadAttendanceStatus() {
    const token = localStorage.getItem('token');
    if (token) {
      this.attendanceService.getStatus().subscribe({
        next: (status: AttendanceStatusDto) => {
          this.isCheckedIn = status.isCheckedIn || false;
          this.checkInTime = status.checkInTime || null;
        },
        error: (err) => {
          console.error('Error loading attendance status:', err);
        }
      });
    }
  }

  toggleAttendance() {
    if (this.isLoading) return;
    this.isLoading = true;

    if (this.isCheckedIn) {
      this.attendanceService.checkOut().subscribe({
        next: () => {
          this.isCheckedIn = false;
          this.checkInTime = null;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error checking out:', err);
          this.isLoading = false;
        }
      });
    } else {
      this.attendanceService.checkIn().subscribe({
        next: (attendance) => {
          this.isCheckedIn = true;
          this.checkInTime = attendance.checkInTime || null;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error checking in:', err);
          this.isLoading = false;
        }
      });
    }
  }

  getCheckInTimeFormatted(): string {
    if (!this.checkInTime) return '';
    const date = new Date(this.checkInTime);
    return date.toLocaleTimeString('ro-RO', { hour: '2-digit', minute: '2-digit' });
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  checkMobile() {
    this.isMobile = window.innerWidth < 768;
  }

  toggleDarkMode(event: any): void {
    const wantsDarkMode = event.checked;

    // If switching to light mode, show warning
    if (!wantsDarkMode) {
      const confirmed = confirm(
        '⚠️ Atenție!\n\n' +
        'Tema luminoasă poate afecta ochii, mai ales în condiții de lumină scăzută.\n\n' +
        'Sunteți sigur că doriți să activați tema luminoasă?'
      );

      if (!confirmed) {
        // Reset toggle to dark mode
        event.source.checked = true;
        return;
      }
    }

    this.isDarkMode = wantsDarkMode;
    localStorage.setItem('darkMode', String(this.isDarkMode));
    this.applyTheme();
  }

  applyTheme(): void {
    const body = document.body.classList;
    if (this.isDarkMode) {
      body.add('dark-theme');
    } else {
      body.remove('dark-theme');
    }
  }

  protected readonly localStorage = localStorage;
}
