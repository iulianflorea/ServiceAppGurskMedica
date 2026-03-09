import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';
import { TicketDto } from '../dtos/ticketDto';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-ticket-list',
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatChipsModule,
    MatSnackBarModule,
    RouterModule,
  ]
})
export class TicketListComponent implements OnInit {

  displayedColumns: string[] = [
    'id', 'clinicName', 'city', 'equipmentBrand', 'equipmentModel',
    'serialNumber', 'phone', 'email', 'problem', 'createdAt', 'status', 'delete'
  ];

  dataSource = new MatTableDataSource<TicketDto>([]);
  publicUrl = `${environment.apiUrl.replace('/api', '').replace('https://doc.', 'https://gursk.')}/ticket-form`;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  statusOptions = [
    { value: 'IN_ASTEPTARE', label: 'În așteptare' },
    { value: 'IN_LUCRU',     label: 'În lucru' },
    { value: 'TERMINAT',     label: 'Terminat' },
  ];

  private readonly statusOrder: Record<string, number> = {
    IN_ASTEPTARE: 0,
    IN_LUCRU: 1,
    TERMINAT: 2,
  };

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.loadTickets();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadTickets() {
    this.http.get<TicketDto[]>(`${environment.apiUrl}/api/tickets`).subscribe({
      next: data => this.dataSource.data = this.sortByStatus(data),
      error: () => this.snackBar.open('Eroare la încărcarea tichetelor.', 'OK', { duration: 3000 })
    });
  }

  updateStatus(ticket: TicketDto, status: string) {
    this.http.patch<TicketDto>(
      `${environment.apiUrl}/api/tickets/${ticket.id}/status?status=${status}`, {}
    ).subscribe({
      next: updated => {
        const idx = this.dataSource.data.findIndex(t => t.id === updated.id);
        if (idx !== -1) {
          const data = [...this.dataSource.data];
          data[idx] = updated;
          this.dataSource.data = this.sortByStatus(data);
        }
      },
      error: () => this.snackBar.open('Eroare la actualizarea statusului.', 'OK', { duration: 3000 })
    });
  }

  delete(ticket: TicketDto) {
    if (!confirm(`Sigur doriți să ștergeți tichetul #${ticket.id} - ${ticket.clinicName}?`)) return;
    this.http.delete(`${environment.apiUrl}/api/tickets/${ticket.id}`).subscribe({
      next: () => {
        this.dataSource.data = this.dataSource.data.filter(t => t.id !== ticket.id);
      },
      error: () => this.snackBar.open('Eroare la ștergere.', 'OK', { duration: 3000 })
    });
  }

  copyUrl() {
    navigator.clipboard.writeText(this.getPublicUrl()).then(() => {
      this.snackBar.open('Link copiat!', '', { duration: 2000 });
    });
  }

  getPublicUrl(): string {
    return window.location.origin + '/ticket-form';
  }

  private sortByStatus(data: TicketDto[]): TicketDto[] {
    return [...data].sort((a, b) =>
      (this.statusOrder[a.status ?? ''] ?? 99) - (this.statusOrder[b.status ?? ''] ?? 99)
    );
  }

  statusLabel(status: string | undefined): string {
    switch (status) {
      case 'IN_ASTEPTARE': return 'În așteptare';
      case 'IN_LUCRU':     return 'În lucru';
      case 'TERMINAT':     return 'Terminat';
      default:             return status || '';
    }
  }

  formatDate(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('ro-RO', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }
}
