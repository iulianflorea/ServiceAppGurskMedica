import {ChangeDetectionStrategy, Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {EmployeeDto} from "../dtos/employeeDto";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {SignaturePadComponent} from "../signature-pad/signature-pad.component";
import {map, Observable, startWith} from "rxjs";
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";


@Component({
  selector: 'app-intervention-sheet-form',
  templateUrl: './intervention-sheet-form.component.html',
  styleUrls: ['./intervention-sheet-form.component.css'],



})
export class InterventionSheetFormComponent implements OnInit {
  id: any;
  equipmentSelected: any;
  equipmentList: EquipmentDto[] = [];
  dateOfIntervention: string | undefined = "";
  dateOfExpireWarranty: string | undefined = "";
  yearsOfWarranty: any;
  customerSelected: any;
  customerList: CustomerDto[] = [];
  employeeSelected: any;
  employeeList: EmployeeDto[] = [];
  typeOfInterventionSelected: any;
  typeOfInterventionList: string[] = [];
  serialNumber: any;
  noticed: any;
  fixed: any;
  engineerNote: any;

  @ViewChild(SignaturePadComponent) signaturePadComponent!: SignaturePadComponent;
  signatureBase64: string = '';

  customerControl = new FormControl();
  employeeControl = new FormControl();
  equipmentControl = new FormControl();
  typeControl = new FormControl();

  filteredCustomers!: Observable<CustomerDto[]>;
  filteredEmployees!: Observable<EmployeeDto[]>;
  filteredEquipment!: Observable<EquipmentDto[]>;
  filteredTypes!: Observable<string[]>;
  isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);


  interventionSheetForm: FormGroup = new FormGroup({
    serialNumber: new FormControl(),
    noticed: new FormControl(),
    fixed: new FormControl(),
    engineerNote: new FormControl(),
    yearsOfWarranty: new FormControl()
  });

  constructor(private httpClient: HttpClient, private router: Router, private route: ActivatedRoute) {
  }


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

    this.filteredEmployees = this.employeeControl.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : value?.name),
      map(name => name ? this.filterEmployees(name) : this.employeeList.slice())
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

    if (this.route.snapshot.params['id'] !== undefined) {
      this.httpClient.get("/api/intervention-sheet/" + this.route.snapshot.params['id']).subscribe((response: InterventionSheetDto) => {
        this.id = response.id;
        this.customerSelected = response.customerId;
        this.customerControl.setValue(response.customerName);
        this.employeeSelected = response.employeeId;
        this.employeeControl.setValue(response.employeeName);
        this.equipmentSelected = response.equipmentId;
        this.equipmentControl.setValue(response.equipmentName);
        this.typeOfInterventionSelected = response.typeOfIntervention;
        this.typeControl.setValue(response.typeOfIntervention);
        this.serialNumber = response.serialNumber;
        this.noticed = response.noticed;
        this.fixed = response.fixed;
        this.engineerNote = response.engineerNote;
        this.dateOfIntervention = response.dateOfIntervention;
        this.dateOfExpireWarranty = response.dateOfExpireWarranty;
        this.yearsOfWarranty = response.yearsOfWarranty;
      });
    }
  }


  getEquipmentList() {
    this.httpClient.get("/api/equipment/find-all").subscribe((response) => {
      console.log(response);
      this.equipmentList = response as EquipmentDto[];
    })
  }

  getCustomerList() {
    this.httpClient.get("/api/customer/customer-list").subscribe((response) => {
      console.log(response);
      this.customerList = response as CustomerDto[];
    })
  }

  getEmployeeList() {
    this.httpClient.get("/api/employee/find-all").subscribe((response) => {
      console.log(response);
      this.employeeList = response as EmployeeDto[];
    })
  }

  getType() {
    this.httpClient.get<string[]>("/api/intervention-sheet/type").subscribe((data) => {
      this.typeOfInterventionList = data;
    })
  }


  saveInterventionSheet() {
    this.captureSignature();
    // const customerId = this.getCustomerIdByName(this.customerControl.value);

    const interventionSheet: any = {
      typeOfIntervention: this.typeControl.value,
      dateOfIntervention: this.dateOfIntervention ? this.convertDatePiker(new Date(this.dateOfIntervention)) : null,
      dateOfExpireWarranty: this.dateOfExpireWarranty,
      yearsOfWarranty: this.yearsOfWarranty,
      serialNumber: this.serialNumber,
      noticed: this.noticed,
      fixed: this.fixed,
      engineerNote: this.engineerNote,
      equipmentId: this.equipmentList.find(eq => eq.model === this.equipmentControl.value)?.id,
      customerId: this.customerList.find(c => c.name === this.customerControl.value)?.id,
      employeeId: this.employeeList.find(e => e.name === this.employeeControl.value)?.id,
      signatureBase64: this.signatureBase64
    };

    if (this.id) {
      interventionSheet.id = this.id; // doar dacă e definit
    }

    this.httpClient.post("/api/intervention-sheet", interventionSheet).subscribe((response) => {
      alert("Intervention sheet was saved");
      this.router.navigate(["/intervention-sheet-list"]);
    });
  }


  getById(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    this.httpClient.get("/api/intervention-sheet/" + id).subscribe((response) => {
      console.log(response);
    })
  }

  convertDatePiker(date?: any) {
    if (!date || isNaN(new Date(date).getTime())) {
      console.error("convertDatePiker: Invalid date", date);
      return null;
    }
    date = new Date(date);
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date.toISOString().substring(0, 10);
  }

  captureSignature() {
    if (this.signaturePadComponent && !this.signaturePadComponent.isEmpty()) {
      this.signatureBase64 = this.signaturePadComponent.getSignatureImage();
    }
  }


  private filterCustomers(name: string): CustomerDto[] {
    // @ts-ignore
    return this.customerList.filter(c => c.name.toLowerCase().includes(name.toLowerCase()));
  }

  private filterEmployees(name: string): EmployeeDto[] {
    // @ts-ignore
    return this.employeeList.filter(e => e.name.toLowerCase().includes(name.toLowerCase()));
  }

  private filterEquipment(name: string): EquipmentDto[] {
    // @ts-ignore
    return this.equipmentList.filter(eq => eq.model.toLowerCase().includes(name.toLowerCase()));
  }

  private filterTypes(value: string): string[] {
    return this.typeOfInterventionList.filter(t => t.toLowerCase().includes(value));
  }

}



