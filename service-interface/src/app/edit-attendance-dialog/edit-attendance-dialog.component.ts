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

import { AttendanceService } from '../services/attendance.service';
import { AttendanceDto } from '../dtos/attendanceDto';

@Component({
  selector: 'app-edit-attendance-dialog',
  templateUrl: './edit-attendance-dialog.component.html',
  styleUrls: ['./edit-attendance-dialog.component.css'],
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
    MatIconModule
  ]
})
export class EditAttendanceDialogComponent implements OnInit {

  form: FormGroup;
  isLoading = false;
  attendance: AttendanceDto;

  hours: string[] = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0'));
  minutes: string[] = Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0'));

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private dialogRef: MatDialogRef<EditAttendanceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { attendance: AttendanceDto }
  ) {
    this.attendance = data.attendance;
    this.form = this.fb.group({
      date: [null, Validators.required],
      checkInHour: ['09', Validators.required],
      checkInMinute: ['00', Validators.required],
      checkOutHour: ['18', Validators.required],
      checkOutMinute: ['00', Validators.required],
      notes: ['']
    });
  }

  ngOnInit() {
    this.populateForm();
  }

  populateForm() {
    if (this.attendance.date) {
      this.form.patchValue({ date: new Date(this.attendance.date) });
    }

    if (this.attendance.checkInTime) {
      const checkIn = new Date(this.attendance.checkInTime);
      this.form.patchValue({
        checkInHour: String(checkIn.getHours()).padStart(2, '0'),
        checkInMinute: String(checkIn.getMinutes()).padStart(2, '0')
      });
    }

    if (this.attendance.checkOutTime) {
      const checkOut = new Date(this.attendance.checkOutTime);
      this.form.patchValue({
        checkOutHour: String(checkOut.getHours()).padStart(2, '0'),
        checkOutMinute: String(checkOut.getMinutes()).padStart(2, '0')
      });
    }

    if (this.attendance.notes) {
      this.form.patchValue({ notes: this.attendance.notes });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.form.invalid || !this.attendance.id) return;

    this.isLoading = true;

    const formValue = this.form.value;
    const date = new Date(formValue.date);
    const dateStr = this.formatDate(date);

    const checkInTime = `${dateStr}T${formValue.checkInHour}:${formValue.checkInMinute}:00`;
    const checkOutTime = `${dateStr}T${formValue.checkOutHour}:${formValue.checkOutMinute}:00`;

    const dto: AttendanceDto = {
      id: this.attendance.id,
      date: dateStr,
      checkInTime: checkInTime,
      checkOutTime: checkOutTime,
      notes: formValue.notes
    };

    this.attendanceService.updateAttendance(this.attendance.id, dto).subscribe({
      next: () => {
        this.isLoading = false;
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Error updating attendance:', err);
        this.isLoading = false;
        alert('Eroare la actualizarea pontajului');
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
