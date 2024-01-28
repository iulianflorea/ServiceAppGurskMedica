import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeeDto} from "../dtos/employeeDto";

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.css']
})
export class EmployeeFormComponent implements OnInit {

  id: any;
  name: any;
  cnp: any;


  employeeForm: FormGroup = new FormGroup({
    name: new FormControl,
    cnp: new FormControl
  })

  constructor(private httpClient: HttpClient,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    console.log("id", this.route.snapshot.params['id']);
    if (this.route.snapshot.params['id'] !== undefined) {
      this.httpClient.get("/api/employee/find-by-id/" + this.route.snapshot.params['id']).subscribe((response: EmployeeDto) => {
        console.log(response);
        this.id = this.route.snapshot.params['id'];
        this.name = response.name;
        this.cnp = response.cnp
      })
    }
  }

  saveEmployee() {
    var employee = {
      id: this.id,
      name: this.name,
      cnp: this.cnp
    }
    this.httpClient.post("/api/employee", employee).subscribe((response) => {
      console.log(response);
      alert("Employee was saved");
      this.router.navigate(["/employee-list"]);
    })
  }
}
