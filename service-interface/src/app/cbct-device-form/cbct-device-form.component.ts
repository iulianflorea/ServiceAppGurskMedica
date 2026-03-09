import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CbctDeviceDto, CbctReferenceDto } from '../dtos/cbctDto';
import { environment } from '../../environments/environment';

export interface ReferenceRow {
  mode: string;
  gender: string;
  kvp: number | null;
  current: number | null;
  scanTime: number | null;
  dap: number | null;
}

@Component({
  selector: 'app-cbct-device-form',
  templateUrl: './cbct-device-form.component.html',
  styleUrls: ['./cbct-device-form.component.css']
})
export class CbctDeviceFormComponent implements OnInit {

  modeGenders: Record<string, string[]> = {
    CT:    ['BARBAT', 'FEMEIE', 'COPIL'],
    PANO:  ['BARBAT', 'FEMEIE', 'COPIL'],
    CEPH:  ['BARBAT', 'FEMEIE', 'COPIL'],
    RETRO: ['ADULT', 'COPIL']
  };

  deviceId: number | null = null;
  brand = '';
  model = '';
  rows: ReferenceRow[] = [];
  saving = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.initRows();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.deviceId = +id;
      this.loadDevice(this.deviceId);
    }
  }

  initRows() {
    this.rows = [];
    for (const mode of Object.keys(this.modeGenders)) {
      for (const gender of this.modeGenders[mode]) {
        this.rows.push({ mode, gender, kvp: null, current: null, scanTime: null, dap: null });
      }
    }
  }

  loadDevice(id: number) {
    this.http.get<CbctDeviceDto>(`${environment.apiUrl}/api/cbct/devices/${id}`, { headers: this.authHeaders() }).subscribe({
      next: d => {
        this.brand = d.brand;
        this.model = d.model;
        for (const row of this.rows) {
          const ref = d.references?.find(r => r.mode === row.mode && r.gender === row.gender);
          if (ref) {
            row.kvp = ref.kvp ?? null;
            row.current = ref.current ?? null;
            row.scanTime = ref.scanTime ?? null;
            row.dap = ref.dap ?? null;
          }
        }
      }
    });
  }

  save() {
    if (!this.brand || !this.model) {
      this.snackBar.open('Completați brand și model.', 'OK', { duration: 3000 });
      return;
    }
    this.saving = true;
    const dto: CbctDeviceDto = {
      id: this.deviceId || undefined,
      brand: this.brand,
      model: this.model,
      references: this.rows.map(r => ({
        mode: r.mode,
        gender: r.gender,
        kvp: r.kvp,
        current: r.current,
        scanTime: r.scanTime,
        dap: r.dap
      }) as CbctReferenceDto)
    };
    this.http.post<CbctDeviceDto>(`${environment.apiUrl}/api/cbct/devices`, dto, { headers: this.authHeaders() }).subscribe({
      next: saved => {
        this.deviceId = saved.id || null;
        this.snackBar.open('Salvat!', '', { duration: 1500 });
        this.saving = false;
      },
      error: () => {
        this.snackBar.open('Eroare la salvare.', 'OK', { duration: 3000 });
        this.saving = false;
      }
    });
  }

  back() {
    this.router.navigate(['/cbct-devices']);
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }
}
