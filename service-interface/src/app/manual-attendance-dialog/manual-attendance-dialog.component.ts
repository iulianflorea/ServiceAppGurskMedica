import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { AttendanceService } from '../services/attendance.service';
import { ManualAttendanceDto } from '../dtos/attendanceDto';
import { UserDto } from '../dtos/userDto';

@Component({
  selector: 'app-manual-attendance-dialog',
  templateUrl: './manual-attendance-dialog.component.html',
  styleUrls: ['./manual-attendance-dialog.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatCheckboxModule
  ]
})
export class ManualAttendanceDialogComponent implements OnInit {

  form: FormGroup;
  users: UserDto[] = [];
  isAdmin = false;
  isLoading = false;
  isMobile = window.innerWidth <= 768;
  includeCheckOut = false;

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private dialogRef: MatDialogRef<ManualAttendanceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { users: UserDto[], isAdmin: boolean }
  ) {
    this.isAdmin = data?.isAdmin || false;
    this.form = this.fb.group({
      userId: [null, this.isAdmin ? Validators.required : []],
      date: [null, Validators.required],
      checkInHour: ['09', Validators.required],
      checkInMinute: ['30', Validators.required],
      checkOutHour: ['18'],
      checkOutMinute: ['00'],
      notes: ['']
    });
  }

  ngOnInit() {
    this.users = this.data?.users || [];
  }

  hours: string[] = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0'));
  minutes: string[] = Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0'));

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.isLoading = true;

    const formValue = this.form.value;
    const date = new Date(formValue.date);
    const dateStr = this.formatDate(date);

    const checkInTime = `${dateStr}T${formValue.checkInHour}:${formValue.checkInMinute}:00`;
    const checkOutTime = this.includeCheckOut
      ? `${dateStr}T${formValue.checkOutHour}:${formValue.checkOutMinute}:00`
      : undefined;

    const dto: ManualAttendanceDto = {
      userId: formValue.userId,
      date: dateStr,
      checkInTime: checkInTime,
      checkOutTime: checkOutTime,
      notes: formValue.notes
    };

    const request$ = this.isAdmin
      ? this.attendanceService.createManualAttendance(dto)
      : this.attendanceService.createMyManualAttendance(dto);

    request$.subscribe({
      next: () => {
        this.isLoading = false;
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Error creating manual attendance:', err);
        this.isLoading = false;
        alert('Eroare la salvarea pontajului');
      }
    });
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
