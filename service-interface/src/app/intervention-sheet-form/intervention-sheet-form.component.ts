import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {EmployeeDto} from "../dtos/employeeDto";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";


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
    this.getEquipmentList();
    this.getCustomerList();
    this.getEmployeeList();
    this.getType();
    console.log("id", this.route.snapshot.params['id'])
    if (this.route.snapshot.params['id'] !== undefined) {
      this.httpClient.get("/api/intervention-sheet/" + this.route.snapshot.params['id']).subscribe((response: InterventionSheetDto) => {
        console.log(response);
        // @ts-ignore
        this.id = response.id;
        this.customerSelected = response.customerId;
        this.employeeSelected = response.employeeId;
        this.equipmentSelected = response.equipmentId;
        this.typeOfInterventionSelected = response.typeOfIntervention;
        this.serialNumber = response.serialNumber;
        this.noticed = response.noticed;
        this.fixed = response.fixed;
        this.engineerNote = response.engineerNote;
        this.dateOfIntervention = response.dateOfIntervention;
        this.dateOfExpireWarranty = response.dateOfExpireWarranty;
        this.yearsOfWarranty = response.yearsOfWarranty;
      })
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
    var interventionSheet = {
      id: this.id,
      typeOfIntervention: this.typeOfInterventionSelected,
      dateOfIntervention: this.dateOfIntervention,
      dateOfExpireWarranty: this.dateOfExpireWarranty,
      yearsOfWarranty: this.yearsOfWarranty,
      serialNumber: this.serialNumber,
      noticed: this.noticed,
      fixed: this.fixed,
      engineerNote: this.engineerNote,
      equipmentId: this.equipmentSelected,
      customerId: this.customerSelected,
      employeeId: this.employeeSelected
    }
    this.httpClient.post("/api/intervention-sheet", interventionSheet).subscribe((response) => {
      console.log(response);
      alert("Intervention sheet was saved");
      this.router.navigate(["/intervention-sheet-list"]);
    })
  }

  getById(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    this.httpClient.get("/api/intervention-sheet/" + id).subscribe((response) => {
      console.log(response);
    })
  }

}
