import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin, of } from 'rxjs';
import {
  CbctDeviceDto, CbctDozimetrieDto,
  CbctMeasurementDto, CbctMeasurementValueDto, CbctReferenceDto
} from '../dtos/cbctDto';
import { CustomerDto } from '../dtos/customerDto';
import { environment } from '../../environments/environment';

export interface MeasurementRow {
  mode: string;
  gender: string;
  ref: CbctReferenceDto | null;
  kvp: number | null;
  scanTime: number | null;
  mgy: number | null;
  mmAiHvl: number | null;
  uGyPerS: number | null;
  pulses: number | null;
  mmAiTf: number | null;
}

export interface DozimetrieRow {
  punctMasurat: string;
  valoareaMaximaMarsurata: number | null;
  materialPerete: string;
}

@Component({
  selector: 'app-cbct-measurement-form',
  templateUrl: './cbct-measurement-form.component.html',
  styleUrls: ['./cbct-measurement-form.component.css']
})
export class CbctMeasurementFormComponent implements OnInit {

  modeGenders: Record<string, string[]> = {
    CT:    ['BARBAT', 'FEMEIE', 'COPIL'],
    PANO:  ['BARBAT', 'FEMEIE', 'COPIL'],
    CEPH:  ['BARBAT', 'FEMEIE', 'COPIL'],
    RETRO: ['ADULT', 'COPIL']
  };

  measurementId: number | null = null;
  selectedDeviceId: number | null = null;
  selectedCustomerId: number | null = null;
  serialNumber = '';
  measurementDate: Date | null = new Date();

  devices: CbctDeviceDto[] = [];
  customers: CustomerDto[] = [];
  filteredCustomers: CustomerDto[] = [];
  filteredDevices: CbctDeviceDto[] = [];
  customerSearchText = '';
  deviceSearchText = '';
  rows: MeasurementRow[] = [];
  dozimetrieRows: DozimetrieRow[] = [];
  selectedDevice: CbctDeviceDto | null = null;
  saving = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    const measurementId = id ? +id : null;

    const devices$ = this.http.get<CbctDeviceDto[]>(`${environment.apiUrl}/api/cbct/devices`, { headers: this.authHeaders() });
    const customers$ = this.http.get<CustomerDto[]>(`${environment.apiUrl}/customer/customer-list`, { headers: this.authHeaders() });
    const measurement$ = measurementId
      ? this.http.get<CbctMeasurementDto>(`${environment.apiUrl}/api/cbct/measurements/${measurementId}`, { headers: this.authHeaders() })
      : of(null);

    forkJoin({ devices: devices$, customers: customers$, measurement: measurement$ }).subscribe({
      next: ({ devices, customers, measurement }) => {
        this.devices = devices;
        this.customers = customers;
        this.filteredCustomers = customers;
        this.filteredDevices = devices;
        if (measurement) {
          this.measurementId = measurement.id || null;
          this.selectedCustomerId = measurement.customerId || null;
          this.selectedDeviceId = measurement.deviceId || null;
          this.serialNumber = measurement.serialNumber || '';
          this.measurementDate = measurement.measurementDate ? new Date(measurement.measurementDate) : null;
          this.selectedDevice = devices.find(d => d.id === this.selectedDeviceId) || null;
          const cust = customers.find(c => c.id === this.selectedCustomerId);
          this.customerSearchText = cust?.name || '';
          this.deviceSearchText = this.selectedDevice ? `${this.selectedDevice.brand} ${this.selectedDevice.model}` : '';
          this.initRowsFromMeasurement(measurement);
          this.dozimetrieRows = (measurement.dozimetrie || []).map(d => ({
            punctMasurat: d.punctMasurat || '',
            valoareaMaximaMarsurata: d.valoareaMaximaMarsurata ?? null,
            materialPerete: d.materialPerete || ''
          }));
          if (this.dozimetrieRows.length === 0) this.dozimetrieRows.push({ punctMasurat: '', valoareaMaximaMarsurata: null, materialPerete: '' });
        } else {
          this.initRows(null);
          this.dozimetrieRows = [{ punctMasurat: '', valoareaMaximaMarsurata: null, materialPerete: '' }];
        }
      },
      error: () => this.snackBar.open('Eroare la incarcate.', 'OK', { duration: 3000 })
    });
  }

  filterCustomers(text: string) {
    const lower = (text || '').toLowerCase();
    this.filteredCustomers = this.customers.filter(c => (c.name || '').toLowerCase().includes(lower));
  }

  filterDevices(text: string) {
    const lower = (text || '').toLowerCase();
    this.filteredDevices = this.devices.filter(d =>
      `${d.brand} ${d.model}`.toLowerCase().includes(lower)
    );
  }

  onCustomerSelected(name: string) {
    const c = this.customers.find(x => x.name === name);
    this.selectedCustomerId = c?.id || null;
    this.customerSearchText = name;
  }

  onDeviceSelected(displayName: string) {
    const d = this.devices.find(x => `${x.brand} ${x.model}` === displayName);
    this.selectedDeviceId = d?.id || null;
    this.deviceSearchText = displayName;
    this.onDeviceChange();
  }

  onDeviceChange() {
    this.selectedDevice = this.devices.find(d => d.id === this.selectedDeviceId) || null;
    this.initRows(this.selectedDevice);
  }

  initRows(device: CbctDeviceDto | null) {
    this.rows = [];
    for (const mode of Object.keys(this.modeGenders)) {
      for (const gender of this.modeGenders[mode]) {
        const ref = device?.references?.find(r => r.mode === mode && r.gender === gender) || null;
        this.rows.push({ mode, gender, ref, kvp: null, scanTime: null, mgy: null, mmAiHvl: null, uGyPerS: null, pulses: null, mmAiTf: null });
      }
    }
  }

  initRowsFromMeasurement(m: CbctMeasurementDto) {
    const device = this.devices.find(d => d.id === m.deviceId) || null;
    this.rows = [];
    for (const mode of Object.keys(this.modeGenders)) {
      for (const gender of this.modeGenders[mode]) {
        const ref = device?.references?.find(r => r.mode === mode && r.gender === gender) || null;
        const val = m.values?.find(v => v.mode === mode && v.gender === gender) || null;
        this.rows.push({
          mode, gender, ref,
          kvp: val?.kvp ?? null, scanTime: val?.scanTime ?? null, mgy: val?.mgy ?? null,
          mmAiHvl: val?.mmAiHvl ?? null, uGyPerS: val?.uGyPerS ?? null,
          pulses: val?.pulses ?? null, mmAiTf: val?.mmAiTf ?? null
        });
      }
    }
  }

  addDozimetrieRow() {
    this.dozimetrieRows.push({ punctMasurat: '', valoareaMaximaMarsurata: null, materialPerete: '' });
  }

  removeDozimetrieRow(index: number) {
    if (this.dozimetrieRows.length > 1) {
      this.dozimetrieRows.splice(index, 1);
    }
  }

  autosave() { this.save(true); }

  save(silent = false) {
    if (!this.selectedDeviceId || !this.selectedCustomerId) {
      if (!silent) this.snackBar.open('Selectati clientul si aparatul.', 'OK', { duration: 3000 });
      return;
    }
    this.saving = true;
    const dto: CbctMeasurementDto = {
      id: this.measurementId || undefined,
      customerId: this.selectedCustomerId,
      deviceId: this.selectedDeviceId,
      serialNumber: this.serialNumber,
      measurementDate: this.measurementDate ? this.formatDate(this.measurementDate) : null,
      values: this.rows.map(r => ({ mode: r.mode, gender: r.gender, kvp: r.kvp, scanTime: r.scanTime, mgy: r.mgy, mmAiHvl: r.mmAiHvl, uGyPerS: r.uGyPerS, pulses: r.pulses, mmAiTf: r.mmAiTf }) as CbctMeasurementValueDto),
      dozimetrie: this.dozimetrieRows
        .filter(d => d.punctMasurat || d.valoareaMaximaMarsurata != null || d.materialPerete)
        .map(d => ({ punctMasurat: d.punctMasurat, valoareaMaximaMarsurata: d.valoareaMaximaMarsurata, materialPerete: d.materialPerete }) as CbctDozimetrieDto)
    };

    this.http.post<CbctMeasurementDto>(`${environment.apiUrl}/api/cbct/measurements`, dto, { headers: this.authHeaders() }).subscribe({
      next: saved => {
        this.measurementId = saved.id || null;
        if (!silent) this.snackBar.open('Salvat!', '', { duration: 1500 });
        this.saving = false;
      },
      error: () => {
        this.snackBar.open('Eroare la salvare.', 'OK', { duration: 3000 });
        this.saving = false;
      }
    });
  }

  exportExcel() {
    if (!this.measurementId) { this.snackBar.open('Salvati mai intai masuratorea.', 'OK', { duration: 3000 }); return; }
    this.http.get(`${environment.apiUrl}/api/cbct/measurements/${this.measurementId}/excel`,
      { headers: this.authHeaders(), responseType: 'blob' }
    ).subscribe({
      next: blob => this.downloadBlob(blob, `masuratori_cbct_${this.measurementId}.xlsx`),
      error: () => this.snackBar.open('Eroare la export Excel.', 'OK', { duration: 3000 })
    });
  }

  exportPdf() {
    if (!this.measurementId) { this.snackBar.open('Salvati mai intai masuratorea.', 'OK', { duration: 3000 }); return; }
    this.http.get(`${environment.apiUrl}/api/cbct/measurements/${this.measurementId}/pdf`,
      { headers: this.authHeaders(), responseType: 'blob' }
    ).subscribe({
      next: blob => this.downloadBlob(blob, `masuratori_cbct_${this.measurementId}.pdf`),
      error: () => this.snackBar.open('Eroare la export PDF.', 'OK', { duration: 3000 })
    });
  }

  back() { this.router.navigate(['/cbct-list']); }

  private downloadBlob(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename; a.click();
    window.URL.revokeObjectURL(url);
  }

  private formatDate(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }
}
