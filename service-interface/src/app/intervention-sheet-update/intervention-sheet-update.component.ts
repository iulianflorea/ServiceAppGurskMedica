import {Component, OnInit} from '@angular/core';
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {EmployeeDto} from "../dtos/employeeDto";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {InterventionSheetListComponent} from "../intervention-sheet-list/intervention-sheet-list.component";

@Component({
  selector: 'app-intervention-sheet-update',
  templateUrl: './intervention-sheet-update.component.html',
  styleUrls: ['./intervention-sheet-update.component.css']
})
export class InterventionSheetUpdateComponent implements OnInit{
  equipmentSelected: any;
  equipmentList: EquipmentDto[] = [];
  dateOfIntervention: Date = new Date();
  customerSelected: any;
  customerList: CustomerDto[] = [];
  employeeSelected: any;
  employeeList: EmployeeDto[] = [];
  typeOfInterventionSelected: any;
  typeOfInterventionList: string[] = [];



  interventionSheetForm: FormGroup = new FormGroup({
    serialNumber: new FormControl(),
    noticed: new FormControl(),
    fixed: new FormControl(),
    engineerNote: new FormControl()
  });

  constructor(private httpClient: HttpClient) {
  }

  ngOnInit() {
    this.getEquipmentList();
    this.getCustomerList();
    this.getEmployeeList();
    this.getType();
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
    this.httpClient.get("/api/employee/find-list").subscribe((response) => {
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
    this.httpClient.post("/api/intervention-sheet", interventionSheet).subscribe((response) => {
      console.log(response);
      alert("Intervention sheet was saved");
    })
  }

  update(interventionSheet: InterventionSheetDto) {
    this.httpClient.put("/api/intervention-sheet/update", interventionSheet).subscribe((response) =>{
      console.log(response);
    })
  }

  getById(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    this.httpClient.get("/api/intervention-sheet/" + id).subscribe((response) => {
      console.log(response);

    })
  }
}
