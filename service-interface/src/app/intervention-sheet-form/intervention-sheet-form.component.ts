import {Component, isStandalone} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {EmployeeDto} from "../dtos/employeeDto";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {getXHRResponse} from "rxjs/internal/ajax/getXHRResponse";

@Component({
  selector: 'app-intervention-sheet-form',
  templateUrl: './intervention-sheet-form.component.html',
  styleUrls: ['./intervention-sheet-form.component.css']
})
export class InterventionSheetFormComponent {
  equipmentSelected: any;
  equipmentList: EquipmentDto[] = [];
  // dateOfIntervention: Date = new Date();
  customerSelected: any;
  customerList: CustomerDto[] = [];
  employeeSelected: any;
  employeeList: EmployeeDto[] = [];



  interventionSheetForm : FormGroup = new FormGroup({
    // typeOfIntervention: new FormControl(),
    serialNumber: new FormControl(),
    noticed: new FormControl(),
    fixed: new FormControl(),
    engineerNote: new FormControl()
  });

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  ngOnInit() {
    // this.getEquipmentList();
    this.getCustomerList();
    this.getEmployeeList();


  }

  // getEquipmentList() {
  //   this.httpClient.get("/api/equipment/equipment-list").subscribe((response) =>{
  //     console.log(response);
  //     this.equipmentList = response as EquipmentDto[];
  //   })
  // }

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

  saveInterventionSheet() {
    var interventionSheet = {
      typeOfIntervention: this.interventionSheetForm.value.typeOfIntervention,
      // dateOfIntervention: this.dateOfIntervention,
      serialNumber: this.interventionSheetForm.value.serialNumber,
      noticed: this.interventionSheetForm.value.noticed,
      fixed: this.interventionSheetForm.value.fixed,
      engineerNote: this.interventionSheetForm.value.engineerNote,
      // equipmentId: this.equipmentSelected,
      customerId: this.customerSelected,
      employeeId: this.employeeSelected
    }
    this.httpClient.post("/api/intervention-sheet", interventionSheet).subscribe((response) =>{
      console.log(response);
      alert("Intervention sheet was saved");

    })

  }


  protected readonly isStandalone = isStandalone;
}
