import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CbctDeviceDto } from '../dtos/cbctDto';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-cbct-device-list',
  templateUrl: './cbct-device-list.component.html',
  styleUrls: ['./cbct-device-list.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSnackBarModule,
  ]
})
export class CbctDeviceListComponent implements OnInit {

  devices: CbctDeviceDto[] = [];
  displayedColumns = ['brand', 'model', 'actions'];

  constructor(private http: HttpClient, private router: Router, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.http.get<CbctDeviceDto[]>(`${environment.apiUrl}/api/cbct/devices`, { headers: this.authHeaders() }).subscribe({
      next: data => this.devices = data,
      error: () => this.snackBar.open('Eroare la încărcare.', 'OK', { duration: 3000 })
    });
  }

  edit(id: number) {
    this.router.navigate(['/cbct-device-form', id]);
  }

  delete(d: CbctDeviceDto) {
    if (!confirm(`Ștergi aparatul ${d.brand} ${d.model}?`)) return;
    this.http.delete(`${environment.apiUrl}/api/cbct/devices/${d.id}`, { headers: this.authHeaders() }).subscribe({
      next: () => this.devices = this.devices.filter(x => x.id !== d.id),
      error: () => this.snackBar.open('Eroare la ștergere.', 'OK', { duration: 3000 })
    });
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }
}
