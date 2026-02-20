import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

export interface VehicleDeleteDialogData {
  licensePlate: string;
  makeModel: string;
}

@Component({
  selector: 'app-vehicle-delete-confirm-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ],
  template: `
    <div class="delete-dialog">
      <div class="delete-dialog-header">
        <mat-icon class="warn-icon">warning_amber</mat-icon>
        <h2>Ștergere mașină</h2>
      </div>

      <mat-dialog-content>
        <p class="vehicle-label">
          <strong>{{ data.licensePlate }}</strong>
          <span *ngIf="data.makeModel"> — {{ data.makeModel }}</span>
        </p>
        <div class="warning-box">
          <mat-icon>info</mat-icon>
          <span>
            Atenție! Vor fi șterse definitiv <strong>toate informațiile</strong> legate
            de această mașină: ITP, asigurare, revizii tehnice, evenimente și documente/fotografii.
            Această acțiune este <strong>ireversibilă</strong>.
          </span>
        </div>

        <mat-form-field appearance="outline" class="confirm-field">
          <mat-label>Tastează <strong>DELETE</strong> pentru a confirma</mat-label>
          <input matInput [(ngModel)]="confirmText" autocomplete="off" />
        </mat-form-field>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-stroked-button (click)="cancel()">Anulează</button>
        <button mat-raised-button color="warn"
                [disabled]="confirmText !== 'DELETE'"
                (click)="confirm()">
          <mat-icon>delete_forever</mat-icon> Șterge definitiv
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .delete-dialog {
      min-width: 360px;
      max-width: 480px;
    }
    .delete-dialog-header {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 20px 24px 0;
    }
    .delete-dialog-header h2 {
      margin: 0;
      font-size: 1.1rem;
      font-weight: 600;
    }
    .warn-icon {
      color: #e53935;
      font-size: 28px;
      width: 28px;
      height: 28px;
    }
    .vehicle-label {
      font-size: 1rem;
      margin: 8px 0 12px;
      color: #333;
    }
    .warning-box {
      display: flex;
      gap: 10px;
      align-items: flex-start;
      background: #fff3e0;
      border: 1px solid #ffcc80;
      border-radius: 8px;
      padding: 12px 14px;
      font-size: 0.875rem;
      color: #5d4037;
      margin-bottom: 20px;
    }
    .warning-box mat-icon {
      color: #f57c00;
      flex-shrink: 0;
      margin-top: 1px;
    }
    .confirm-field {
      width: 100%;
    }
  `]
})
export class VehicleDeleteConfirmDialogComponent {
  confirmText = '';

  constructor(
    private dialogRef: MatDialogRef<VehicleDeleteConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VehicleDeleteDialogData
  ) {}

  cancel() {
    this.dialogRef.close(false);
  }

  confirm() {
    if (this.confirmText === 'DELETE') {
      this.dialogRef.close(true);
    }
  }
}
