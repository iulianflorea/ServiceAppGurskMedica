import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {EmployeeDto} from "../dtos/employeeDto";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";




@Component({
  selector: 'app-intervention-sheet-form',
  templateUrl: './intervention-sheet-form.component.html',
  styleUrls: ['./intervention-sheet-form.component.css'],

})
export class InterventionSheetFormComponent implements OnInit{
  equipmentSelected: any;
  equipmentList: EquipmentDto[] = [];
  dateOfIntervention: Date = new Date();
  customerSelected: any;
  customerList: CustomerDto[] = [];
  employeeSelected: any;
  employeeList: EmployeeDto[] = [];
  typeOfInterventionSelected: any;
  typeOfInterventionList: string[] = [];




  interventionSheetForm : FormGroup = new FormGroup({
    serialNumber: new FormControl(),
    noticed: new FormControl(),
    fixed: new FormControl(),
    engineerNote: new FormControl()
  });

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  ngOnInit() {
    this.getEquipmentList();
    this.getCustomerList();
    this.getEmployeeList();
    this.getType();
  }

  getEquipmentList() {
    this.httpClient.get("/api/equipment/find-all").subscribe((response) =>{
      console.log(response);
      this.equipmentList = response as EquipmentDto[];
    })
  }

  getCustomerList() {
    this.httpClient.get("/api/customer/customer-list").subscribe((response) =>{
      console.log(response);
      this.customerList = response as CustomerDto[];
    })
  }

  getEmployeeList() {
    this.httpClient.get("/api/employee/find-list").subscribe((response) =>{
      console.log(response);
      this.employeeList = response as EmployeeDto[];
    })
  }

  getType() {
    this.httpClient.get<string[]>("/api/intervention-sheet/type").subscribe((data) =>{
      this.typeOfInterventionList = data;
    })
  }

  saveInterventionSheet() {
    var interventionSheet = {
      typeOfIntervention: this.typeOfInterventionSelected,
      dateOfIntervention: this.dateOfIntervention,
      serialNumber: this.interventionSheetForm.value.serialNumber,
      noticed: this.interventionSheetForm.value.noticed,
      fixed: this.interventionSheetForm.value.fixed,
      engineerNote: this.interventionSheetForm.value.engineerNote,
      equipmentId: this.equipmentSelected,
      customerId: this.customerSelected,
      employeeId: this.employeeSelected
    }
    this.httpClient.post("/api/intervention-sheet", interventionSheet).subscribe((response) =>{
      console.log(response);
      alert("Intervention sheet was saved");

    })
  }


}
