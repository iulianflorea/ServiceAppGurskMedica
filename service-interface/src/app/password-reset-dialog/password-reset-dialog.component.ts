import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';

import { UserService } from '../services/user.service';
import { UserDto } from '../dtos/userDto';

@Component({
  selector: 'app-password-reset-dialog',
  templateUrl: './password-reset-dialog.component.html',
  styleUrls: ['./password-reset-dialog.component.css'],
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
    MatIconModule
  ]
})
export class PasswordResetDialogComponent implements OnInit {

  form: FormGroup;
  users: UserDto[] = [];
  isLoading = false;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private dialogRef: MatDialogRef<PasswordResetDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { users: UserDto[] }
  ) {
    this.form = this.fb.group({
      userId: [null, Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit() {
    this.users = this.data?.users || [];
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.isLoading = true;

    const formValue = this.form.value;

    this.userService.resetPassword(formValue.userId, formValue.newPassword).subscribe({
      next: () => {
        this.isLoading = false;
        this.dialogRef.close(true);
        alert('Parola a fost resetata cu succes!');
      },
      error: (err) => {
        console.error('Error resetting password:', err);
        this.isLoading = false;
        alert('Eroare la resetarea parolei');
      }
    });
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }
}
