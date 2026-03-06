import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import {
  VehicleDto,
  VehicleItpDto,
  VehicleInsuranceDto,
  VehicleRevisionDto,
  VehicleEventDto
} from '../dtos/vehicleDto';
import { UserDto } from '../dtos/userDto';
import { VehicleDocumentService } from '../services/vehicle-document.service';
import { environment } from '../../environments/environment';

export interface DocumentItem {
  name: string;
  isImage: boolean;
  displayUrl?: string;
}

@Component({
  selector: 'app-vehicle-form',
  templateUrl: './vehicle-form.component.html',
  styleUrls: ['./vehicle-form.component.css'],
})
export class VehicleFormComponent implements OnInit {

  id: number | null = null;
  isMobile = window.innerWidth <= 768;

  userList: UserDto[] = [];

  fuelTypes = ['Benzina', 'Diesel', 'Electric', 'Hibrid', 'GPL'];
  eventTypes = ['Schimb ulei', 'Schimb anvelope', 'Service', 'Reparație', 'Altele'];

  // Photo
  photoPreviewUrl: string | null = null;
  photoFile: File | null = null;

  // Sub-lists
  itpList: VehicleItpDto[] = [];
  insuranceList: VehicleInsuranceDto[] = [];
  revisions: VehicleRevisionDto[] = [];
  events: VehicleEventDto[] = [];

  // Mini-forms
  newItp: VehicleItpDto = {};
  newInsurance: VehicleInsuranceDto = {};
  newRevision: VehicleRevisionDto = {};
  newEvent: VehicleEventDto = {};

  // Date pickers (ngModel Date objects for mat-datepicker)
  newItpDate: Date | null = null;
  newInsuranceDate: Date | null = null;
  newRevisionDate: Date | null = null;
  newEventDate: Date | null = null;

  // Documents
  documents: DocumentItem[] = [];
  lightboxOpen = false;
  lightboxImageUrl = '';

  vehicleForm: FormGroup = new FormGroup({
    licensePlate: new FormControl('', Validators.required),
    vin: new FormControl(''),
    make: new FormControl('', Validators.required),
    model: new FormControl('', Validators.required),
    year: new FormControl(null),
    color: new FormControl(''),
    fuelType: new FormControl(''),
    engineCapacity: new FormControl(null),
    power: new FormControl(null),
    currentKm: new FormControl(null),
    userId: new FormControl(null),
    notes: new FormControl(''),
  });

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private docService: VehicleDocumentService
  ) {}

  ngOnInit() {
    this.isMobile = window.innerWidth <= 768;
    this.loadUsers();

    const routeId = this.route.snapshot.params['id'];
    if (routeId) {
      this.id = +routeId;
      this.http.get<VehicleDto>(`${environment.apiUrl}/api/vehicles/${routeId}`).subscribe(v => {
        this.vehicleForm.patchValue({
          licensePlate: v.licensePlate,
          vin: v.vin,
          make: v.make,
          model: v.model,
          year: v.year,
          color: v.color,
          fuelType: v.fuelType,
          engineCapacity: v.engineCapacity,
          power: v.power,
          currentKm: v.currentKm,
          userId: v.userId,
          notes: v.notes,
        });
        this.itpList = v.itpList || [];
        this.insuranceList = v.insuranceList || [];
        this.revisions = v.revisions || [];
        this.events = v.events || [];

        if (v.photoName) {
          this.docService.getPhoto(this.id!).subscribe({
            next: blob => { this.photoPreviewUrl = URL.createObjectURL(blob); },
            error: () => {}
          });
        }
      });
      this.loadDocuments();
    }
  }

  loadUsers() {
    this.http.get<UserDto[]>(`${environment.apiUrl}/user/findAll`).subscribe(users => {
      this.userList = users;
    });
  }

  // ── Save ────────────────────────────────────────────────────────────

  save() {
    const dto: VehicleDto = this.vehicleForm.value;
    const formData = new FormData();
    formData.append('data', JSON.stringify(dto));
    if (this.photoFile) {
      formData.append('photo', this.photoFile);
    }

    if (this.id) {
      this.http.put<VehicleDto>(`${environment.apiUrl}/api/vehicles/${this.id}`, formData).subscribe(() => {
        alert('Mașina a fost salvată.');
        this.router.navigate(['/vehicle-list']);
      });
    } else {
      this.http.post<VehicleDto>(`${environment.apiUrl}/api/vehicles`, formData).subscribe(v => {
        alert('Mașina a fost adăugată.');
        this.router.navigate(['/vehicle-list']);
      });
    }
  }

  // ── Photo ────────────────────────────────────────────────────────────

  onPhotoSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.photoFile = file;
      const reader = new FileReader();
      reader.onload = () => { this.photoPreviewUrl = reader.result as string; };
      reader.readAsDataURL(file);
    }
  }

  deletePhoto() {
    if (!this.id) { this.photoPreviewUrl = null; this.photoFile = null; return; }
    if (confirm('Ștergi fotografia?')) {
      this.http.delete(`${environment.apiUrl}/api/vehicles/${this.id}/photo`).subscribe(() => {
        this.photoPreviewUrl = null;
        this.photoFile = null;
      });
    }
  }

  // ── ITP ─────────────────────────────────────────────────────────────

  calcItpExpiry(): string {
    if (this.newItpDate && this.newItp.validityMonths) {
      const d = new Date(this.newItpDate);
      d.setMonth(d.getMonth() + this.newItp.validityMonths);
      d.setDate(d.getDate() - 1);
      return this.formatDate(d);
    }
    return '';
  }

  addItp() {
    if (!this.id || !this.newItpDate) return;
    this.newItp.date = this.formatDate(this.newItpDate);
    this.http.post<VehicleItpDto>(`${environment.apiUrl}/api/vehicles/${this.id}/itp`, this.newItp)
      .subscribe(itp => {
        this.itpList.push(itp);
        this.newItp = {};
        this.newItpDate = null;
      });
  }

  deleteItp(id: number) {
    if (!confirm('Ștergi această înregistrare ITP?')) return;
    this.http.delete(`${environment.apiUrl}/api/vehicles/itp/${id}`).subscribe(() => {
      this.itpList = this.itpList.filter(i => i.id !== id);
    });
  }

  // ── Insurance ────────────────────────────────────────────────────────

  calcInsuranceExpiry(): string {
    if (this.newInsuranceDate && this.newInsurance.validityMonths) {
      const d = new Date(this.newInsuranceDate);
      d.setMonth(d.getMonth() + this.newInsurance.validityMonths);
      d.setDate(d.getDate() - 1);
      return this.formatDate(d);
    }
    return '';
  }

  addInsurance() {
    if (!this.id || !this.newInsuranceDate) return;
    this.newInsurance.date = this.formatDate(this.newInsuranceDate);
    this.http.post<VehicleInsuranceDto>(`${environment.apiUrl}/api/vehicles/${this.id}/insurance`, this.newInsurance)
      .subscribe(ins => {
        this.insuranceList.push(ins);
        this.newInsurance = {};
        this.newInsuranceDate = null;
      });
  }

  deleteInsurance(id: number) {
    if (!confirm('Ștergi această asigurare?')) return;
    this.http.delete(`${environment.apiUrl}/api/vehicles/insurance/${id}`).subscribe(() => {
      this.insuranceList = this.insuranceList.filter(i => i.id !== id);
    });
  }

  // ── Revision ─────────────────────────────────────────────────────────

  addRevision() {
    if (!this.id || !this.newRevisionDate) return;
    this.newRevision.date = this.formatDate(this.newRevisionDate);
    this.http.post<VehicleRevisionDto>(`${environment.apiUrl}/api/vehicles/${this.id}/revisions`, this.newRevision)
      .subscribe(r => {
        this.revisions.push(r);
        this.newRevision = {};
        this.newRevisionDate = null;
      });
  }

  deleteRevision(id: number) {
    if (!confirm('Ștergi această revizie?')) return;
    this.http.delete(`${environment.apiUrl}/api/vehicles/revisions/${id}`).subscribe(() => {
      this.revisions = this.revisions.filter(r => r.id !== id);
    });
  }

  // ── Events ───────────────────────────────────────────────────────────

  addEvent() {
    if (!this.id || !this.newEventDate) return;
    this.newEvent.date = this.formatDate(this.newEventDate);
    this.http.post<VehicleEventDto>(`${environment.apiUrl}/api/vehicles/${this.id}/events`, this.newEvent)
      .subscribe(e => {
        this.events.push(e);
        this.newEvent = {};
        this.newEventDate = null;
      });
  }

  deleteEvent(id: number) {
    if (!confirm('Ștergi acest eveniment?')) return;
    this.http.delete(`${environment.apiUrl}/api/vehicles/events/${id}`).subscribe(() => {
      this.events = this.events.filter(e => e.id !== id);
    });
  }

  // ── Documents ─────────────────────────────────────────────────────────

  loadDocuments() {
    if (!this.id) return;
    this.docService.getDocuments(this.id).subscribe({
      next: (docs) => {
        this.documents = docs.map(doc => ({
          name: doc.name,
          isImage: /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(doc.name),
        }));
        this.documents.forEach(doc => {
          if (doc.isImage) {
            const url = `${environment.apiUrl}/api/vehicles/${this.id}/documents/${encodeURIComponent(doc.name)}`;
            this.http.get(url, {
              headers: this.docService.getAuthHeaders(),
              responseType: 'blob'
            }).subscribe(blob => { doc.displayUrl = URL.createObjectURL(blob); });
          }
        });
      },
      error: err => console.error('Error loading documents', err)
    });
  }

  get imageDocuments(): DocumentItem[] { return this.documents.filter(d => d.isImage); }
  get otherDocuments(): DocumentItem[] { return this.documents.filter(d => !d.isImage); }

  openPhotoLightbox(doc: DocumentItem) {
    if (doc.displayUrl) { this.lightboxImageUrl = doc.displayUrl; this.lightboxOpen = true; }
  }

  closeLightbox() { this.lightboxOpen = false; }

  onDocumentSelected(event: any) {
    const file: File = event.target.files[0];
    if (file && this.id) {
      this.docService.uploadDocument(this.id, file).subscribe({
        next: () => this.loadDocuments(),
        error: err => console.error('Upload failed', err)
      });
    }
  }

  deleteDocument(filename: string, event: Event) {
    event.stopPropagation();
    if (!this.id) return;
    if (confirm('Ștergi fișierul?')) {
      this.docService.deleteDocument(this.id, filename).subscribe({
        next: () => this.loadDocuments(),
        error: err => console.error('Delete failed', err)
      });
    }
  }

  previewDocument(doc: DocumentItem) {
    if (!this.id) return;
    const url = `${environment.apiUrl}/api/vehicles/${this.id}/documents/${encodeURIComponent(doc.name)}`;
    this.http.get(url, { headers: this.docService.getAuthHeaders(), responseType: 'blob' })
      .subscribe(blob => { window.open(URL.createObjectURL(blob), '_blank'); });
  }

  downloadDocument(doc: DocumentItem) {
    if (!this.id) return;
    this.docService.downloadDocument(this.id, doc.name).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url; a.download = doc.name; a.click();
        URL.revokeObjectURL(url);
      },
      error: err => console.error('Download failed', err)
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  private formatDate(d: Date): string {
    const year = d.getFullYear();
    const month = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  isExpiringSoon(dateStr: string | undefined): boolean {
    if (!dateStr) return false;
    const expiry = new Date(dateStr);
    const today = new Date();
    const diffDays = (expiry.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
    return diffDays <= 30;
  }
}
