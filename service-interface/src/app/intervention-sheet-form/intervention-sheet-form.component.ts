import { Component } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {EquipmentDto} from "../dtos/equipmentDto";
import {CustomerDto} from "../dtos/customerDto";
import {EmployeeDto} from "../dtos/employeeDto";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-intervention-sheet-form',
  templateUrl: './intervention-sheet-form.component.html',
  styleUrls: ['./intervention-sheet-form.component.css']
})
export class InterventionSheetFormComponent {
  equipmentSelected: any;
  equipmentList: EquipmentDto[] = [];
  dateOfIntervention: Date = new Date();
  customerSelected: any;
  customerList: CustomerDto[] = [];
  employeeSelected: any;
  employeeList: EmployeeDto[] = [];



  interventionSheetForm : FormGroup = new FormGroup({
    typeOfIntervention: new FormControl(),
    serialNumber: new FormControl(),
    noticed: new FormControl(),
    fixed: new FormControl(),
    engineerNote: new FormControl()
  });

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  ngOnInit() {
    this.getEquipmentList();

  }

  getEquipmentList() {
    this.httpClient.get("/api/equipment-list").subscribe((response) =>{
      console.log(response);
      this.equipmentList = response as EquipmentDto[];
    })
  }

  getCustomerList() {
    this.httpClient.get("/api/customer-list").subscribe((response) =>{
      console.log(response);
      this.customerList = response as CustomerDto[];
    })
  }







}
