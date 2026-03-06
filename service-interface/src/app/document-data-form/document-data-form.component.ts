import {AfterViewInit, Component, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {FormBuilder, FormGroup, FormControl, FormArray, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Observable, forkJoin, startWith, map} from 'rxjs';
import {SignaturePadComponent} from '../signature-pad/signature-pad.component';
import {environment} from "../../environments/environment";
import {DocumentEquipmentDto, DocumentProductDto, DocumentTrainedPersonDto} from "../dtos/documentDataDto";
import {MatSnackBar} from '@angular/material/snack-bar';

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

interface ProductDto {
  id: number;
  name: string;
  cod: string;
}

@Component({
  selector: 'app-document-data-form',
  templateUrl: './document-data-form.component.html',
  styleUrls: ['./document-data-form.component.css']
})
export class DocumentDataFormComponent implements OnInit {

  id?: number;
  documentForm!: FormGroup;
  loading = false;
  isMobile = window.innerWidth <= 768;

  customerList: CustomerDto[] = [];
  filteredCustomers!: Observable<CustomerDto[]>;

  equipmentList: EquipmentDto[] = [];
  filteredEquipmentOptions: Observable<EquipmentDto[]>[] = [];

  productList: ProductDto[] = [];
  filteredProductOptions: Observable<ProductDto[]>[] = [];

  @ViewChild(SignaturePadComponent) signaturePadComponent!: SignaturePadComponent;
  @ViewChildren('trainedPersonSignaturePad') trainedPersonSignaturePads!: QueryList<SignaturePadComponent>;

  contractDate: Date | null = null;
  signatureDate: Date | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
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
      contactPerson: [''],
      equipments: this.fb.array([]),
      products: this.fb.array([]),
      trainedPersons: this.fb.array([])
    });

    // Load customers, equipments & products
    forkJoin({
      customers: this.http.get<CustomerDto[]>(`${environment.apiUrl}/customer/customer-list`),
      equipments: this.http.get<EquipmentDto[]>(`${environment.apiUrl}/equipment/find-all`),
      products: this.http.get<ProductDto[]>(`${environment.apiUrl}/product/getAll`)
    }).subscribe(({customers, equipments, products}) => {
      this.customerList = customers || [];
      this.equipmentList = equipments || [];
      this.productList = products || [];

      this.filteredCustomers = this.documentForm.get('customerControl')!.valueChanges.pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value?.name),
        map(name => name ? this._filterCustomers(name) : this.customerList.slice())
      );

      // Add one empty equipment row by default
      if (!this.id) {
        this.addEquipment();
        this.addProduct();
        this.addTrainedPerson();
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

  onContractDateChange(event: any) {
    if (event.value) {
      this.documentForm.get('contractDate')?.setValue(this.formatDate(event.value));
    }
  }

  onSignatureDateChange(event: any) {
    if (event.value) {
      this.documentForm.get('signatureDate')?.setValue(this.formatDate(event.value));
    }
  }

  get equipmentsArray(): FormArray {
    return this.documentForm.get('equipments') as FormArray;
  }

  get trainedPersonsArray(): FormArray {
    return this.documentForm.get('trainedPersons') as FormArray;
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

  get productsArray(): FormArray {
    return this.documentForm.get('products') as FormArray;
  }

  addProduct(): void {
    const productGroup = this.fb.group({
      id: [null],
      productControl: new FormControl(null),
      productName: [''],
      productCod: [''],
      quantity: [1]
    });

    const filteredOptions = productGroup.get('productControl')!.valueChanges.pipe(
      startWith(''),
      map((value: any) => typeof value === 'string' ? value : value?.name),
      map((name: string) => name ? this._filterProducts(name) : this.productList.slice())
    );
    this.filteredProductOptions.push(filteredOptions);

    productGroup.get('productControl')!.valueChanges.subscribe((val: any) => {
      if (val && typeof val === 'object' && 'id' in val) {
        productGroup.get('productName')?.setValue(val.name || '');
        productGroup.get('productCod')?.setValue(val.cod || '');
      }
    });

    this.productsArray.push(productGroup);
  }

  removeProduct(index: number): void {
    this.productsArray.removeAt(index);
    this.filteredProductOptions.splice(index, 1);
  }

  displayProduct(pr?: ProductDto): string {
    return pr ? pr.name : '';
  }

  private _filterProducts(name: string): ProductDto[] {
    const filterValue = name.toLowerCase();
    return this.productList.filter(p => p.name.toLowerCase().includes(filterValue) || p.cod.toLowerCase().includes(filterValue));
  }

  addTrainedPerson(): void {
    const group = this.fb.group({
      id: [null],
      trainedPersonName: [''],
      jobFunction: [''],
      phone: [''],
      email: [''],
      signatureBase64: ['']
    });
    this.trainedPersonsArray.push(group);
  }

  removeTrainedPerson(index: number): void {
    this.trainedPersonsArray.removeAt(index);
  }

  clearTrainedPersonSignature(index: number): void {
    const pads = this.trainedPersonSignaturePads?.toArray();
    if (pads && pads[index]) {
      pads[index].clear();
    }
    const group = this.trainedPersonsArray.at(index) as FormGroup;
    group.get('signatureBase64')?.setValue('');
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
        contactPerson: doc.contactPerson
      });

      if (doc.contractDate) {
        this.contractDate = new Date(doc.contractDate);
      }
      if (doc.signatureDate) {
        this.signatureDate = new Date(doc.signatureDate);
      }

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

      // Load products
      if (doc.products && doc.products.length > 0) {
        doc.products.forEach((pr: DocumentProductDto) => {
          this.addProduct();
          const lastIndex = this.productsArray.length - 1;
          const group = this.productsArray.at(lastIndex) as FormGroup;

          group.get('id')?.setValue(pr.id || null);
          const prObj = this.productList.find(p => p.id === pr.productId);
          if (prObj) {
            group.get('productControl')?.setValue(prObj, {emitEvent: false});
          }
          group.get('productName')?.setValue(pr.productName || '');
          group.get('productCod')?.setValue(pr.productCod || '');
          group.get('quantity')?.setValue(pr.quantity ?? 1);
        });
      }

      // Load trained persons
      if (doc.trainedPersons && doc.trainedPersons.length > 0) {
        doc.trainedPersons.forEach((tp: DocumentTrainedPersonDto) => {
          this.addTrainedPerson();
          const lastIndex = this.trainedPersonsArray.length - 1;
          const group = this.trainedPersonsArray.at(lastIndex) as FormGroup;

          group.get('id')?.setValue(tp.id || null);
          group.get('trainedPersonName')?.setValue(tp.trainedPersonName || '');
          group.get('jobFunction')?.setValue(tp.jobFunction || '');
          group.get('phone')?.setValue(tp.phone || '');
          group.get('email')?.setValue(tp.email || '');
          group.get('signatureBase64')?.setValue(tp.signatureBase64 || '');
        });
      } else {
        this.addTrainedPerson();
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

    // Build products array
    const products: DocumentProductDto[] = [];
    for (let i = 0; i < this.productsArray.length; i++) {
      const prGroup = this.productsArray.at(i).value;
      const prControl = prGroup.productControl;

      if (prControl?.id || prGroup.productName || prGroup.productCod) {
        products.push({
          id: prGroup.id || null,
          productId: prControl?.id || null,
          productName: prGroup.productName || '',
          productCod: prGroup.productCod || '',
          quantity: prGroup.quantity || 1,
          sortOrder: i
        });
      }
    }

    // Build trained persons array with signatures
    const trainedPersons: DocumentTrainedPersonDto[] = [];
    const pads = this.trainedPersonSignaturePads?.toArray() || [];
    for (let i = 0; i < this.trainedPersonsArray.length; i++) {
      const tpGroup = this.trainedPersonsArray.at(i).value;

      // Only add if has any data
      if (tpGroup.trainedPersonName || tpGroup.jobFunction || tpGroup.phone || tpGroup.email) {
        let sig = tpGroup.signatureBase64 || '';
        // Capture signature from pad if available and not empty
        if (pads[i] && !pads[i].isEmpty()) {
          sig = pads[i].getSignatureImage();
        }

        trainedPersons.push({
          id: tpGroup.id || null,
          trainedPersonName: tpGroup.trainedPersonName || '',
          jobFunction: tpGroup.jobFunction || '',
          phone: tpGroup.phone || '',
          email: tpGroup.email || '',
          signatureBase64: sig,
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
      contactPerson: form.contactPerson,
      equipments: equipments,
      products: products,
      trainedPersons: trainedPersons
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

  downloadDocx(id: number, type: string): void {
    this.loading = true;
    this.http.get(`${environment.apiUrl}/documents/export/${id}/${type}`, { responseType: 'blob' })
      .subscribe({
        next: (blob) => {
          const fileName = `${type}_${id}.docx`;
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = fileName;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(url);
          this.loading = false;
          this.snackBar.open(`Documentul ${type} a fost generat cu succes.`, 'OK', { duration: 3000 });
        },
        error: (err) => {
          console.error('Eroare la generarea documentului DOCX:', err);
          this.loading = false;
          this.snackBar.open('Eroare la generarea fișierului DOCX.', 'Închide', { duration: 3000 });
        }
      });
  }
}
