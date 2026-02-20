import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { VehicleDto } from '../dtos/vehicleDto';
import { VehicleDocumentService } from '../services/vehicle-document.service';
import { VehicleDeleteConfirmDialogComponent } from './vehicle-delete-confirm-dialog.component';
import { environment } from '../../environments/environment.prod';

@Component({
  selector: 'app-vehicle-list',
  templateUrl: './vehicle-list.component.html',
  styleUrls: ['./vehicle-list.component.css'],
  standalone: true,
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatButtonModule,
    RouterLink,
    FormsModule,
    CommonModule,
    RouterModule,
    MatInputModule,
    MatTooltipModule,
    MatDialogModule,
    VehicleDeleteConfirmDialogComponent,
  ]
})
export class VehicleListComponent implements OnInit, AfterViewInit {

  displayedColumns: string[] = ['id', 'photo', 'licensePlate', 'makeModel', 'vin', 'userName', 'itpExpiry', 'insuranceExpiry', 'delete'];
  dataSource = new MatTableDataSource<VehicleDto>([]);
  photoUrls = new Map<number, string>();
  keyword: string = '';
  isMobile: boolean = false;
  alertTriggering = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private http: HttpClient,
    private router: Router,
    private docService: VehicleDocumentService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.isMobile = window.innerWidth < 768;
    window.addEventListener('resize', () => {
      this.isMobile = window.innerWidth < 768;
    });
    this.loadAll();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadAll() {
    this.http.get<VehicleDto[]>(`${environment.apiUrl}/api/vehicles`).subscribe(data => {
      this.dataSource.data = data;
      this.loadPhotos(data);
    });
  }

  private loadPhotos(vehicles: VehicleDto[]) {
    vehicles.filter(v => v.photoName && v.id).forEach(v => {
      this.docService.getPhoto(v.id!).subscribe({
        next: blob => {
          const old = this.photoUrls.get(v.id!);
          if (old) URL.revokeObjectURL(old);
          this.photoUrls.set(v.id!, URL.createObjectURL(blob));
        },
        error: () => {}
      });
    });
  }

  getPhotoUrl(vehicle: VehicleDto): string | undefined {
    return this.photoUrls.get(vehicle.id!);
  }

  search() {
    if (this.keyword.trim()) {
      this.http.get<VehicleDto[]>(`${environment.apiUrl}/api/vehicles/search?keyword=${this.keyword}`).subscribe(data => {
        this.dataSource.data = data;
        this.loadPhotos(data);
      });
    } else {
      this.loadAll();
    }
  }

  navigateToEdit(vehicle: VehicleDto) {
    this.router.navigate(['/vehicle-form', vehicle.id]);
  }

  delete(vehicle: VehicleDto, event: Event) {
    event.stopPropagation();

    const ref = this.dialog.open(VehicleDeleteConfirmDialogComponent, {
      data: {
        licensePlate: vehicle.licensePlate,
        makeModel: [vehicle.make, vehicle.model].filter(Boolean).join(' ')
      },
      disableClose: true,
      autoFocus: false
    });

    ref.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.http.delete(`${environment.apiUrl}/api/vehicles/${vehicle.id}`).subscribe(() => {
          this.loadAll();
        });
      }
    });
  }

  triggerAlerts() {
    this.alertTriggering = true;
    this.http.post(`${environment.apiUrl}/api/vehicles/alerts/trigger`, {}, { responseType: 'text' }).subscribe({
      next: () => {
        this.alertTriggering = false;
        alert('Alertele au fost procesate. Verifică inbox-ul email și log-urile serverului.');
      },
      error: () => {
        this.alertTriggering = false;
        alert('Eroare la declanșarea alertelor. Verifică log-urile serverului.');
      }
    });
  }

  isExpiringSoon(dateStr: string | undefined): boolean {
    if (!dateStr) return false;
    const expiry = new Date(dateStr);
    const today = new Date();
    const diffDays = (expiry.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
    return diffDays <= 30;
  }

  getLatestItpExpiry(vehicle: VehicleDto): string | undefined {
    return vehicle.itpList && vehicle.itpList.length > 0
      ? vehicle.itpList[vehicle.itpList.length - 1]?.expiryDate
      : undefined;
  }

  getLatestInsuranceExpiry(vehicle: VehicleDto): string | undefined {
    return vehicle.insuranceList && vehicle.insuranceList.length > 0
      ? vehicle.insuranceList[vehicle.insuranceList.length - 1]?.expiryDate
      : undefined;
  }
}
