import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';

import { AttendanceService } from '../services/attendance.service';
import { WorkScheduleDto } from '../dtos/attendanceDto';

@Component({
  selector: 'app-work-schedule',
  templateUrl: './work-schedule.component.html',
  styleUrls: ['./work-schedule.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule,
    MatSnackBarModule,
    RouterModule
  ]
})
export class WorkScheduleComponent implements OnInit {

  form: FormGroup;
  isLoading = false;
  isSaving = false;

  hours: string[] = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0'));
  minutes: string[] = ['00', '15', '30', '45'];

  weekDays = [
    { value: 'MONDAY', label: 'Luni' },
    { value: 'TUESDAY', label: 'Marți' },
    { value: 'WEDNESDAY', label: 'Miercuri' },
    { value: 'THURSDAY', label: 'Joi' },
    { value: 'FRIDAY', label: 'Vineri' },
    { value: 'SATURDAY', label: 'Sâmbătă' },
    { value: 'SUNDAY', label: 'Duminică' }
  ];

  selectedDays: { [key: string]: boolean } = {};

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      startHour: ['09', Validators.required],
      startMinute: ['30', Validators.required],
      endHour: ['18', Validators.required],
      endMinute: ['00', Validators.required],
      breakMinutes: [30, [Validators.required, Validators.min(0)]],
      standardWorkHours: [8.0, [Validators.required, Validators.min(0)]],
      weekendBonusMultiplier: [1.5, [Validators.required, Validators.min(1)]]
    });

    this.weekDays.forEach(day => {
      this.selectedDays[day.value] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'].includes(day.value);
    });
  }

  ngOnInit() {
    this.loadSchedule();
  }

  loadSchedule() {
    this.isLoading = true;
    this.attendanceService.getWorkSchedule().subscribe({
      next: (schedule) => {
        this.populateForm(schedule);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading schedule:', err);
        this.isLoading = false;
      }
    });
  }

  populateForm(schedule: WorkScheduleDto) {
    if (schedule.startTime) {
      const [h, m] = schedule.startTime.split(':');
      this.form.patchValue({ startHour: h, startMinute: m });
    }
    if (schedule.endTime) {
      const [h, m] = schedule.endTime.split(':');
      this.form.patchValue({ endHour: h, endMinute: m });
    }
    if (schedule.breakMinutes !== undefined) {
      this.form.patchValue({ breakMinutes: schedule.breakMinutes });
    }
    if (schedule.standardWorkHours !== undefined) {
      this.form.patchValue({ standardWorkHours: schedule.standardWorkHours });
    }
    if (schedule.weekendBonusMultiplier !== undefined) {
      this.form.patchValue({ weekendBonusMultiplier: schedule.weekendBonusMultiplier });
    }
    if (schedule.workDays) {
      const days = schedule.workDays.split(',');
      this.weekDays.forEach(day => {
        this.selectedDays[day.value] = days.includes(day.value);
      });
    }
  }

  onSave() {
    if (this.form.invalid) return;

    this.isSaving = true;

    const formValue = this.form.value;
    const workDays = Object.entries(this.selectedDays)
      .filter(([_, selected]) => selected)
      .map(([day, _]) => day)
      .join(',');

    const dto: WorkScheduleDto = {
      startTime: `${formValue.startHour}:${formValue.startMinute}`,
      endTime: `${formValue.endHour}:${formValue.endMinute}`,
      breakMinutes: formValue.breakMinutes,
      standardWorkHours: formValue.standardWorkHours,
      workDays: workDays,
      weekendBonusMultiplier: formValue.weekendBonusMultiplier,
      isActive: true
    };

    this.attendanceService.updateWorkSchedule(dto).subscribe({
      next: () => {
        this.isSaving = false;
        this.snackBar.open('Programul a fost salvat cu succes', 'OK', { duration: 3000 });
      },
      error: (err) => {
        console.error('Error saving schedule:', err);
        this.isSaving = false;
        this.snackBar.open('Eroare la salvarea programului', 'OK', { duration: 3000 });
      }
    });
  }

  toggleDay(day: string) {
    this.selectedDays[day] = !this.selectedDays[day];
  }
}
