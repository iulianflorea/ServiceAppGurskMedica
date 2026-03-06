import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {SignaturePadComponent} from "../signature-pad/signature-pad.component";
import {map, Observable, startWith} from "rxjs";
import {UserDto} from "../dtos/userDto";
import {environment} from "../../environments/environment";
import {DocumentService} from "../services/document.service";
// @ts-ignore
import html2pdf from "html2pdf.js";

export interface DocumentItem {
  name: string;
  isImage: boolean;
  displayUrl?: string;
}

@Component({
  selector: 'app-intervention-sheet-form',
  templateUrl: './intervention-sheet-form.component.html',
  styleUrls: ['./intervention-sheet-form.component.css'],
})
export class InterventionSheetFormComponent implements OnInit {
  id: any;
  equipmentList: EquipmentDto[] = [];
  dateOfIntervention: string | undefined = '';
  dateOfExpireWarranty: string | undefined = '';
  yearsOfWarranty: any;
  customerList: CustomerDto[] = [];
  employeeList: UserDto[] = [];
  typeOfInterventionList: string[] = [];
  serialNumber: any;
  noticed: any;
  fixed: any;
  engineerNote: any;

  @ViewChild(SignaturePadComponent) signaturePadComponent!: SignaturePadComponent;

  signatureBase64: string = '';
  existingSignatureBase64: string = '';
  reSign: boolean = false;

  selectedDateOfIntervention: Date | null = null;

  customerControl = new FormControl();
  employeeControl = new FormControl();
  equipmentControl = new FormControl();
  typeControl = new FormControl();

  filteredCustomers!: Observable<CustomerDto[]>;
  filteredEquipment!: Observable<EquipmentDto[]>;
  filteredTypes!: Observable<string[]>;
  isMobile = window.innerWidth <= 768;

  // Documents
  documents: DocumentItem[] = [];
  lightboxOpen: boolean = false;
  lightboxImageUrl: string = '';

  interventionSheetForm: FormGroup = new FormGroup({
    serialNumber: new FormControl(),
    noticed: new FormControl(),
    fixed: new FormControl(),
    engineerNote: new FormControl(),
    yearsOfWarranty: new FormControl(),
    dateIntervention: new FormControl(null, Validators.required)
  });

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getCustomerList();
    this.getEmployeeList();
    this.getEquipmentList();
    this.getType();
    this.isMobile = window.innerWidth <= 768;

    this.filteredCustomers = this.customerControl.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : value?.name),
      map(name => name ? this.filterCustomers(name) : this.customerList.slice())
    );

    this.filteredEquipment = this.equipmentControl.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : value?.name),
      map(name => name ? this.filterEquipment(name) : this.equipmentList.slice())
    );

    this.filteredTypes = this.typeControl.valueChanges.pipe(
      startWith(''),
      map(value => value?.toLowerCase() || ''),
      map(value => value ? this.filterTypes(value) : this.typeOfInterventionList.slice())
    );

    const routeId = this.route.snapshot.params['id'];
    if (routeId !== undefined) {
      this.httpClient.get(`${environment.apiUrl}/intervention-sheet/` + routeId).subscribe((response: InterventionSheetDto) => {
        this.id = response.id;
        this.customerControl.setValue(response.customerName);
        this.employeeControl.setValue(response.employeeId);
        this.equipmentControl.setValue(response.equipmentName);
        this.typeControl.setValue(response.typeOfIntervention);
        this.serialNumber = response.serialNumber;
        this.noticed = response.noticed;
        this.fixed = response.fixed;
        this.engineerNote = response.engineerNote;
        this.dateOfIntervention = response.dateOfIntervention || '';
        this.dateOfExpireWarranty = response.dateOfExpireWarranty;
        this.yearsOfWarranty = response.yearsOfWarranty;
        this.existingSignatureBase64 = response.signatureBase64 || '';
        if (this.dateOfIntervention) {
          this.selectedDateOfIntervention = new Date(this.dateOfIntervention);
        }
      });

      this.loadDocuments(routeId);
    }
  }

  // ── Documents ──────────────────────────────────────────────────────

  loadDocuments(routeId?: any) {
    const id = routeId || this.id;
    if (!id) return;

    this.documentService.getDocuments(id).subscribe({
      next: (docs) => {
        this.documents = docs.map(doc => ({
          name: doc.name,
          isImage: /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(doc.name),
        }));
        // Fetch blob URLs for images
        this.documents.forEach(doc => {
          if (doc.isImage) {
            const encodedName = encodeURIComponent(doc.name);
            const url = `${environment.apiUrl}/interventions/${id}/documents/${encodedName}`;
            this.httpClient.get(url, {
              headers: this.documentService.getAuthHeaders(),
              responseType: 'blob'
            }).subscribe(blob => {
              doc.displayUrl = URL.createObjectURL(blob);
            });
          }
        });
      },
      error: (err) => console.error('Error loading documents', err)
    });
  }

  get imageDocuments(): DocumentItem[] {
    return this.documents.filter(d => d.isImage);
  }

  get otherDocuments(): DocumentItem[] {
    return this.documents.filter(d => !d.isImage);
  }

  openPhotoLightbox(doc: DocumentItem) {
    if (doc.displayUrl) {
      this.lightboxImageUrl = doc.displayUrl;
      this.lightboxOpen = true;
    }
  }

  closeLightbox() {
    this.lightboxOpen = false;
  }

  onDocumentSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.documentService.uploadDocument(this.id, file).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => console.error('Upload failed', err)
      });
    }
  }

  deleteDocument(filename: string, event: Event) {
    event.stopPropagation();
    if (confirm('Sigur vrei să ștergi acest fișier?')) {
      this.documentService.deleteDocument(this.id, filename).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => console.error('Delete failed', err)
      });
    }
  }

  previewDocument(doc: DocumentItem) {
    const encodedName = encodeURIComponent(doc.name);
    const url = `${environment.apiUrl}/interventions/${this.id}/documents/${encodedName}`;
    this.httpClient.get(url, {
      headers: this.documentService.getAuthHeaders(),
      responseType: 'blob'
    }).subscribe(blob => {
      const fileURL = URL.createObjectURL(blob);
      window.open(fileURL, '_blank');
    });
  }

  downloadDocument(doc: DocumentItem) {
    this.documentService.downloadDocument(this.id, doc.name).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = doc.name;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Download failed', err)
    });
  }

  // ── PDF ────────────────────────────────────────────────────────────

  get selectedEmployeeName(): string {
    if (!this.employeeControl.value) return '';
    const emp = this.employeeList.find(e => e.id === this.employeeControl.value);
    return emp ? `${emp.firstname || ''} ${emp.lastname || ''}`.trim() : '';
  }

  generatePdf() {
    const logoUrl = window.location.origin + '/assets/logo.png';
    const stampUrl = window.location.origin + '/assets/stampila.png';
    const signatureHtml = this.existingSignatureBase64
      ? `<img src="${this.existingSignatureBase64}" style="max-width:200px;max-height:90px;object-fit:contain;" />`
      : '';

    const row = (label: string, value: string) =>
      `<tr>
         <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;font-size:12px;">${label}</td>
         <td style="padding:5px 8px;border:1px solid #ddd;font-size:12px;">${value}</td>
       </tr>`;

    const section = (title: string, text: string) =>
      text ? `<div style="margin-bottom:14px;">
                <div style="font-weight:700;font-size:12px;color:#1a237e;border-bottom:1px solid #e0e0e0;padding-bottom:3px;margin-bottom:6px;text-transform:uppercase;letter-spacing:1px;">${title}</div>
                <div style="font-size:12px;min-height:28px;padding:4px 0;line-height:1.6;">${text}</div>
              </div>` : '';

    const htmlContent = `
      <div style="font-family:Arial,sans-serif;padding:30px 35px;width:714px;background:white;color:#000;box-sizing:border-box;">

        <table style="width:100%;margin-bottom:16px;border-collapse:collapse;">
          <tr>
            <td style="width:160px;vertical-align:middle;">
              <img src="${logoUrl}" style="width:150px;height:auto;" />
            </td>
            <td style="vertical-align:middle;">
              <div style="font-size:22px;font-weight:700;color:#1a237e;letter-spacing:1px;">FIȘĂ DE INTERVENȚIE</div>
              <div style="font-size:13px;color:#555;margin-top:3px;">Nr. ${this.id}</div>
            </td>
          </tr>
        </table>

        <hr style="border:0;border-top:2px solid #1a237e;margin-bottom:18px;" />

        <table style="width:100%;border-collapse:collapse;margin-bottom:20px;">
          <tr>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;width:22%;font-size:12px;">Tip intervenție:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;width:28%;font-size:12px;">${this.typeControl.value || ''}</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;width:22%;font-size:12px;">Data intervenției:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;width:28%;font-size:12px;">${this.dateOfIntervention || ''}</td>
          </tr>
          <tr>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;font-size:12px;">Client:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-size:12px;" colspan="3">${this.customerControl.value || ''}</td>
          </tr>
          <tr>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;font-size:12px;">Tehnician:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-size:12px;">${this.selectedEmployeeName}</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;font-size:12px;">Echipament:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-size:12px;">${this.equipmentControl.value || ''}</td>
          </tr>
          <tr>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;font-size:12px;">Număr serie:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-size:12px;">${this.serialNumber || ''}</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-weight:600;background:#f5f7ff;font-size:12px;">Garanție:</td>
            <td style="padding:5px 8px;border:1px solid #ddd;font-size:12px;">${this.yearsOfWarranty || ''} luni</td>
          </tr>
          ${this.dateOfExpireWarranty ? row('Garanție expiră:', this.dateOfExpireWarranty) : ''}
        </table>

        ${section('Problemă semnalată', this.noticed || '')}
        ${section('Rezolvare', this.fixed || '')}
        ${section('Notă tehnician', this.engineerNote || '')}

        <table style="width:100%;margin-top:60px;border-collapse:collapse;">
          <tr>
            <td style="text-align:center;width:50%;padding-bottom:10px;vertical-align:bottom;">
              <img src="${stampUrl}" style="width:110px;height:auto;" /><br/>
              <span style="font-size:11px;color:#555;">Ștampilă societate</span>
            </td>
            <td style="text-align:center;width:50%;padding-bottom:10px;vertical-align:bottom;">
              <div style="width:200px;height:90px;border-bottom:1px solid #888;margin:0 auto;display:inline-block;vertical-align:bottom;">
                ${signatureHtml}
              </div><br/>
              <span style="font-size:11px;color:#555;">Semnătură client</span>
            </td>
          </tr>
        </table>

      </div>`;

    const options = {
      margin: 0,
      filename: `interventie_${this.id || 'nou'}.pdf`,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    html2pdf().from(htmlContent).set(options).save();
  }

  // ── Form helpers ───────────────────────────────────────────────────

  onDateChange(event: any) {
    const selectedDate: Date | null = event.value;
    if (selectedDate) {
      const year = selectedDate.getFullYear();
      const month = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
      const day = selectedDate.getDate().toString().padStart(2, '0');
      this.dateOfIntervention = `${year}-${month}-${day}`;
    } else {
      this.dateOfIntervention = '';
    }
  }

  getEquipmentList() {
    this.httpClient.get(`${environment.apiUrl}/equipment/find-all`).subscribe((response) => {
      this.equipmentList = response as EquipmentDto[];
    });
  }

  getCustomerList() {
    this.httpClient.get(`${environment.apiUrl}/customer/customer-list`).subscribe((response) => {
      this.customerList = response as CustomerDto[];
    });
  }

  getEmployeeList() {
    this.httpClient.get(`${environment.apiUrl}/user/findAll`).subscribe((response) => {
      this.employeeList = response as UserDto[];
    });
  }

  getType() {
    this.httpClient.get<string[]>(`${environment.apiUrl}/intervention-sheet/type`).subscribe((data) => {
      this.typeOfInterventionList = data;
    });
  }

  captureSignature() {
    if (this.signaturePadComponent && !this.signaturePadComponent.isEmpty()) {
      this.signatureBase64 = this.signaturePadComponent.getSignatureImage();
    }
  }

  saveInterventionSheet() {
    // Use existing signature if not re-signing
    if (!this.reSign && this.existingSignatureBase64 && !this.signatureBase64) {
      this.signatureBase64 = this.existingSignatureBase64;
    } else {
      this.captureSignature();
    }

    const interventionSheet: any = {
      typeOfIntervention: this.typeControl.value,
      dateOfIntervention: this.dateOfIntervention ? this.dateOfIntervention : null,
      dateOfExpireWarranty: this.dateOfExpireWarranty,
      yearsOfWarranty: this.yearsOfWarranty,
      serialNumber: this.serialNumber,
      noticed: this.noticed,
      fixed: this.fixed,
      engineerNote: this.engineerNote,
      equipmentId: this.equipmentList.find(eq => eq.model === this.equipmentControl.value)?.id,
      customerId: this.customerList.find(c => c.name === this.customerControl.value)?.id,
      employeeId: this.employeeControl.value,
      signatureBase64: this.signatureBase64
    };

    if (this.id) {
      interventionSheet.id = this.id;
    }

    this.httpClient.post(`${environment.apiUrl}/intervention-sheet`, interventionSheet).subscribe(() => {
      alert('Intervention sheet was saved');
      this.router.navigate(['/intervention-sheet-list']);
    });
  }

  private filterCustomers(name: string): CustomerDto[] {
    // @ts-ignore
    return this.customerList.filter(c => c.name.toLowerCase().includes(name.toLowerCase()));
  }

  private filterEquipment(name: string): EquipmentDto[] {
    // @ts-ignore
    return this.equipmentList.filter(eq => eq.model.toLowerCase().includes(name.toLowerCase()));
  }

  private filterTypes(value: string): string[] {
    return this.typeOfInterventionList.filter(t => t.toLowerCase().includes(value));
  }
}
