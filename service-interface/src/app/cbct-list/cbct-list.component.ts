import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { CbctMeasurementDto } from '../dtos/cbctDto';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-cbct-list',
  templateUrl: './cbct-list.component.html',
  styleUrls: ['./cbct-list.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatSnackBarModule,
  ]
})
export class CbctListComponent implements OnInit, OnDestroy {

  displayedColumns: string[] = ['id', 'customerName', 'deviceBrand', 'deviceModel', 'serialNumber', 'measurementDate', 'actions'];
  dataSource = new MatTableDataSource<CbctMeasurementDto>([]);
  filterValue = '';
  loading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(private http: HttpClient, private router: Router, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.load();
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(keyword => this.fetch(keyword));
  }

  ngAfterViewInit() {
    this.dataSource.sortingDataAccessor = (item, property) => {
      if (property === 'measurementDate') {
        return item.measurementDate ? new Date(item.measurementDate as any).getTime() : 0;
      }
      return (item as any)[property] ?? '';
    };
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load() {
    this.fetch('');
  }

  applyFilter() {
    this.searchSubject.next(this.filterValue.trim());
  }

  private fetch(keyword: string) {
    this.loading = true;
    const params = keyword ? new HttpParams().set('keyword', keyword) : new HttpParams();
    this.http.get<CbctMeasurementDto[]>(`${environment.apiUrl}/api/cbct/measurements`,
      { headers: this.authHeaders(), params }
    ).subscribe({
      next: data => {
        this.dataSource.data = data;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Eroare la încărcare.', 'OK', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  edit(id: number) {
    this.router.navigate(['/cbct-form', id]);
  }

  delete(m: CbctMeasurementDto) {
    if (!confirm(`Ștergi măsurătoarea #${m.id} — ${m.customerName}?`)) return;
    this.http.delete(`${environment.apiUrl}/api/cbct/measurements/${m.id}`, { headers: this.authHeaders() }).subscribe({
      next: () => this.dataSource.data = this.dataSource.data.filter(x => x.id !== m.id),
      error: () => this.snackBar.open('Eroare la ștergere.', 'OK', { duration: 3000 })
    });
  }

  exportExcel() {
    this.http.get(`${environment.apiUrl}/api/cbct/measurements/export`,
      { headers: this.authHeaders(), responseType: 'blob' }
    ).subscribe({
      next: blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'masuratori_cbct.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.snackBar.open('Eroare la export.', 'OK', { duration: 3000 })
    });
  }

  formatDate(d: string | null | undefined): string {
    if (!d) return '-';
    return new Date(d).toLocaleDateString('ro-RO');
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }
}
