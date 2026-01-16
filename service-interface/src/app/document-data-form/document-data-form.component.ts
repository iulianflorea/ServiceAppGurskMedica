import {Component, OnInit, ViewChild, ElementRef, AfterViewInit} from '@angular/core';
import {FormBuilder, FormGroup, FormControl, FormArray, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Observable, forkJoin, startWith, map} from 'rxjs';
import {SignaturePadComponent} from '../signature-pad/signature-pad.component';
// @ts-ignore
import {Datepicker} from 'vanillajs-datepicker';
import {environment} from "../../environments/environment.prod";
import {DocumentEquipmentDto} from "../dtos/documentDataDto";

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
  filteredEquipmentOptions: Observable<EquipmentDto[]>[] = [];

  @ViewChild(SignaturePadComponent) signaturePadComponent!: SignaturePadComponent;

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

    this.documentForm = this.fb.group({
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
      contactPerson: [''],
      equipments: this.fb.array([])
    });

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
      customers: this.http.get<CustomerDto[]>(`${environment.apiUrl}/customer/customer-list`),
      equipments: this.http.get<EquipmentDto[]>(`${environment.apiUrl}/equipment/find-all`)
    }).subscribe(({customers, equipments}) => {
      this.customerList = customers || [];
      this.equipmentList = equipments || [];

      this.filteredCustomers = this.documentForm.get('customerControl')!.valueChanges.pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value?.name),
        map(name => name ? this._filterCustomers(name) : this.customerList.slice())
      );

      // Add one empty equipment row by default
      if (!this.id) {
        this.addEquipment();
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

  get equipmentsArray(): FormArray {
    return this.documentForm.get('equipments') as FormArray;
  }

  addEquipment(): void {
    const equipmentGroup = this.fb.group({
      id: [null],  // DocumentEquipment ID for existing records
      equipmentControl: new FormControl(null),
      equipmentName: [''],
      productCode: [''],
      serialNumber: ['']
    });

    const index = this.equipmentsArray.length;

    // Setup autocomplete filter for this equipment
    const filteredOptions = equipmentGroup.get('equipmentControl')!.valueChanges.pipe(
      startWith(''),
      map((value: any) => typeof value === 'string' ? value : value?.model),
      map((model: string) => model ? this._filterEquipment(model) : this.equipmentList.slice())
    );
    this.filteredEquipmentOptions.push(filteredOptions);

    // Auto-fill equipment name and product code when equipment is selected
    equipmentGroup.get('equipmentControl')!.valueChanges.subscribe((val: any) => {
      if (val && typeof val === 'object' && 'id' in val) {
        equipmentGroup.get('equipmentName')?.setValue(val.model || '');
        equipmentGroup.get('productCode')?.setValue(val.productCode || '');
      }
    });

    this.equipmentsArray.push(equipmentGroup);
  }

  removeEquipment(index: number): void {
    if (this.equipmentsArray.length > 1) {
      this.equipmentsArray.removeAt(index);
      this.filteredEquipmentOptions.splice(index, 1);
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
    this.http.get<any>(`${environment.apiUrl}/documents/get-by-id/${id}`).subscribe(doc => {
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

      // Load equipments
      if (doc.equipments && doc.equipments.length > 0) {
        doc.equipments.forEach((eq: DocumentEquipmentDto) => {
          this.addEquipment();
          const lastIndex = this.equipmentsArray.length - 1;
          const group = this.equipmentsArray.at(lastIndex) as FormGroup;

          group.get('id')?.setValue(eq.id || null);  // Save DocumentEquipment ID
          const eqObj = this.equipmentList.find(e => e.id === eq.equipmentId);
          if (eqObj) {
            group.get('equipmentControl')?.setValue(eqObj, {emitEvent: false});
          }
          group.get('equipmentName')?.setValue(eq.equipmentName || '');
          group.get('productCode')?.setValue(eq.productCode || '');
          group.get('serialNumber')?.setValue(eq.serialNumber || '');
        });
      } else {
        // Add one empty row if no equipments
        this.addEquipment();
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

    // Build equipments array
    const equipments: DocumentEquipmentDto[] = [];
    for (let i = 0; i < this.equipmentsArray.length; i++) {
      const eqGroup = this.equipmentsArray.at(i).value;
      const eqControl = eqGroup.equipmentControl;

      // Only add if equipment is selected or has data
      if (eqControl?.id || eqGroup.productCode || eqGroup.serialNumber || eqGroup.equipmentName) {
        equipments.push({
          id: eqGroup.id || null,  // DocumentEquipment ID (null for new)
          equipmentId: eqControl?.id || null,
          equipmentName: eqGroup.equipmentName || '',
          productCode: eqGroup.productCode || '',
          serialNumber: eqGroup.serialNumber || '',
          sortOrder: i
        });
      }
    }

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
      contactPerson: form.contactPerson,
      equipments: equipments
    };

    if (this.signaturePadComponent && !this.signaturePadComponent.isEmpty()) {
      dto['signatureBase64'] = this.signaturePadComponent.getSignatureImage();
    }

    this.http.post(`${environment.apiUrl}/documents`, dto).subscribe({
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
