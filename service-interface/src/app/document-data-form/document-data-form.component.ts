import {Component, OnInit, QueryList, ViewChild, ViewChildren, ElementRef, AfterViewInit} from '@angular/core';
import {FormBuilder, FormGroup, FormControl, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Observable, forkJoin, startWith, map, delay} from 'rxjs';
import {SignaturePadComponent} from '../signature-pad/signature-pad.component';
import {MatAutocomplete} from '@angular/material/autocomplete';
// @ts-ignore
import {Datepicker} from 'vanillajs-datepicker';

interface CustomerDto {
  id: number;
  name: string;
  cui: string;
}

interface EquipmentDto {
  id: number;
  model: string;
  productCode: string;
}

@Component({
  selector: 'app-document-data-form',
  templateUrl: './document-data-form.component.html',
  styleUrls: ['./document-data-form.component.css']
})
export class DocumentDataFormComponent implements OnInit, AfterViewInit {

  id?: number;
  documentForm!: FormGroup;
  loading = false;

  customerList: CustomerDto[] = [];
  filteredCustomers!: Observable<CustomerDto[]>;

  equipmentList: EquipmentDto[] = [];
  equipmentControls: FormControl[] = [];
  filteredEquipment: Observable<EquipmentDto[]>[] = [];

  @ViewChild(SignaturePadComponent) signaturePadComponent!: SignaturePadComponent;
  @ViewChildren(MatAutocomplete) equipmentAutos!: QueryList<MatAutocomplete>;

  // FormControls separate pentru date Bootstrap
  @ViewChild('contractDateInput') contractDateInput!: ElementRef<HTMLInputElement>;
  @ViewChild('signatureDateInput') signatureDateInput!: ElementRef<HTMLInputElement>;
  contractDateControl = new FormControl('');
  signatureDateControl = new FormControl('');
  contractDatePicker!: Datepicker;
  signatureDatePicker!: Datepicker;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    // Initialize form
    const group: any = {
      customerControl: new FormControl(null, Validators.required),
      cui: [{value: '', disabled: true}],
      contractDate: [''],
      monthOfWarranty: [''],
      monthOfWarrantyHandPieces: [''],
      numberOfContract: [''],
      signatureDate: [''],
      trainedPerson: [''],
      jobFunction: [''],
      phone: [''],
      email: [''],
      contactPerson: ['']
    };

    for (let i = 1; i <= 6; i++) {
      group['equipmentName' + i] = [''];
      group['productCode' + i] = [''];
      group['serialNumber' + i] = [''];
      this.equipmentControls.push(new FormControl(''));
      this.filteredEquipment.push(new Observable<EquipmentDto[]>());
    }

    this.documentForm = this.fb.group(group);

    // Sincronizare date Bootstrap cu FormControls
    this.contractDateControl.valueChanges.subscribe(val => {
      this.documentForm.get('contractDate')?.setValue(val);
    });
    this.documentForm.get('contractDate')?.valueChanges.subscribe(val => {
      this.contractDateControl.setValue(val, {emitEvent: false});
    });

    this.signatureDateControl.valueChanges.subscribe(val => {
      this.documentForm.get('signatureDate')?.setValue(val);
    });
    this.documentForm.get('signatureDate')?.valueChanges.subscribe(val => {
      this.signatureDateControl.setValue(val, {emitEvent: false});
    });

    // Load customers & equipments
    forkJoin({
      customers: this.http.get<CustomerDto[]>('/api/customer/customer-list'),
      equipments: this.http.get<EquipmentDto[]>('/api/equipment/find-all')
    }).subscribe(({customers, equipments}) => {
      this.customerList = customers || [];
      this.equipmentList = equipments || [];

      this.filteredCustomers = this.documentForm.get('customerControl')!.valueChanges.pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value?.name),
        map(name => name ? this._filterCustomers(name) : this.customerList.slice())
      );

      for (let i = 0; i < 6; i++) {
        const ctrl = this.equipmentControls[i];
        this.documentForm.addControl('equipmentControl' + (i + 1), ctrl);
        this.filteredEquipment[i] = ctrl.valueChanges.pipe(
          startWith(''),
          map(value => typeof value === 'string' ? value : value?.model),
          map(model => model ? this._filterEquipment(model) : this.equipmentList.slice())
        );

        ctrl.valueChanges.subscribe(val => {
          if (val && typeof val === 'object' && 'id' in val) {
            const idx = i + 1;

            // Completezi automat modelul
            this.documentForm.get('equipmentName' + idx)?.setValue(val.model || '');

            // Completezi automat productCode
            this.documentForm.get('productCode' + idx)?.setValue(val.productCode || '');
          } else {
            // Dacă ștergi selecția, golește câmpurile
            const idx = i + 1;
            this.documentForm.get('equipmentName' + idx)?.setValue('');
            this.documentForm.get('productCode' + idx)?.setValue('');
          }
        });

      }

      if (this.id) {
        this.loadDocumentForEdit(this.id);
      }
    });

    // Update CUI when customer changes
    this.documentForm.get('customerControl')!.valueChanges.subscribe(val => {
      if (val && typeof val === 'object') {
        this.documentForm.get('cui')!.setValue(val.cui || '');
      } else {
        this.documentForm.get('cui')!.setValue('');
      }
    });
  }

  ngAfterViewInit(): void {
    const formatLocalDate = (d: Date) => {
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    // CONTRACT DATE
    this.contractDatePicker = new Datepicker(this.contractDateInput.nativeElement, {
      format: 'yyyy-mm-dd',
      autohide: true
    });

    this.contractDateInput.nativeElement.addEventListener('changeDate', () => {
      const date = this.contractDatePicker.getDate();
      if (date) {
        const formatted = formatLocalDate(date);
        this.contractDateControl.setValue(formatted);
        this.documentForm.get('contractDate')?.setValue(formatted);
      }
    });

    // SIGNATURE DATE
    this.signatureDatePicker = new Datepicker(this.signatureDateInput.nativeElement, {
      format: 'yyyy-mm-dd',
      autohide: true
    });

    this.signatureDateInput.nativeElement.addEventListener('changeDate', () => {
      const date = this.signatureDatePicker.getDate();
      if (date) {
        const formatted = formatLocalDate(date);
        this.signatureDateControl.setValue(formatted);
        this.documentForm.get('signatureDate')?.setValue(formatted);
      }
    });
  }


  private onContractDateChange(): void {
    const date = this.contractDatePicker.getDate();
    if (date) {
      this.contractDateControl.setValue(this.formatDate(date));
    }
  }

  private onSignatureDateChange(): void {
    const date = this.signatureDatePicker.getDate();
    if (date) {
      this.signatureDateControl.setValue(this.formatDate(date));
    }
  }

  private _filterCustomers(name: string) {
    const filterValue = name.toLowerCase();
    return this.customerList.filter(c => c.name.toLowerCase().includes(filterValue));
  }

  private _filterEquipment(model: string) {
    const filterValue = model.toLowerCase();
    return this.equipmentList.filter(e => e.model.toLowerCase().includes(filterValue));
  }

  displayCustomer(customer?: CustomerDto): string {
    return customer ? customer.name : '';
  }

  displayEquipment(eq?: EquipmentDto): string {
    return eq ? eq.model : '';
  }

  private formatDate(d: Date | string): string {
    const date = d instanceof Date ? d : new Date(d);
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${dd}`;
  }

  private loadDocumentForEdit(id: number) {
    this.http.get<any>(`/api/documents/get-by-id/${id}`).subscribe(doc => {
      const customer = this.customerList.find(c => c.id === doc.customerId) || {
        id: doc.customerId, name: doc.customerName, cui: doc.cui
      };

      this.documentForm.get('customerControl')!.setValue(customer, {emitEvent: true});

      // Populate rest of fields
      this.documentForm.patchValue({
        contractDate: doc.contractDate,
        monthOfWarranty: doc.monthOfWarranty,
        monthOfWarrantyHandPieces: doc.monthOfWarrantyHandPieces,
        numberOfContract: doc.numberOfContract,
        signatureDate: doc.signatureDate,
        trainedPerson: doc.trainedPerson,
        jobFunction: doc.jobFunction,
        phone: doc.phone,
        email: doc.email,
        contactPerson: doc.contactPerson
      });

      for (let i = 0; i < 6; i++) {
        const eqId = doc['equipmentId' + (i + 1)];
        if (eqId) {
          const eqObj = this.equipmentList.find(e => e.id === eqId);
          if (eqObj) {
            this.equipmentControls[i].setValue(eqObj, {emitEvent: false});
            this.documentForm.get('productCode' + (i + 1))?.setValue(doc['productCode' + (i + 1)] || '');
            this.documentForm.get('serialNumber' + (i + 1))?.setValue(doc['serialNumber' + (i + 1)] || '');
          }
        }
      }
    });
  }

  saveDocument(): void {
    if (this.documentForm.invalid) {
      this.documentForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const form = this.documentForm.value;
    const dto: any = {
      id: this.id || null,
      customerId: form.customerControl?.id || null,
      customerName: form.customerControl?.name || '',
      cui: form.customerControl?.cui || '',
      contractDate: form.contractDate,
      monthOfWarranty: form.monthOfWarranty,
      monthOfWarrantyHandPieces: form.monthOfWarrantyHandPieces,
      numberOfContract: form.numberOfContract,
      signatureDate: form.signatureDate,
      trainedPerson: form.trainedPerson,
      jobFunction: form.jobFunction,
      phone: form.phone,
      email: form.email,
      contactPerson: form.contactPerson
    };

    for (let i = 1; i <= 6; i++) {
      const eqControl = this.documentForm.get('equipmentControl' + i);
      dto['equipmentId' + i] = eqControl?.value?.id || null;
      dto['equipmentName' + i] = form['equipmentName' + i] || '';
      dto['productCode' + i] = form['productCode' + i] || '';
      dto['serialNumber' + i] = form['serialNumber' + i] || '';
    }

    if (this.signaturePadComponent && !this.signaturePadComponent.isEmpty()) {
      dto['signatureBase64'] = this.signaturePadComponent.getSignatureImage();
    }

    this.http.post('/api/documents', dto).subscribe({
      next: () => {
        this.loading = true;
        alert('Document saved');
      },
      error: err => {
        this.loading = false;
        console.error(err);
        alert('Error saving document');
      }
    });
    window.history.back();
  }

  cancel(): void {
    window.history.back();
  }
}
