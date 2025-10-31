import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {CustomerDto} from "../dtos/customerDto";
import {UserDto} from "../dtos/userDto";

@Component({
  selector: 'app-customer-form',
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.css']
})
export class CustomerFormComponent implements OnInit {
  id: any;
  name: any;
  cui: any;
  address: any;
  telephone: any;
  email: any;

  customerForm: FormGroup = new FormGroup({
    name: new FormControl,
    cui: new FormControl,
    address: new FormControl,
    telephone: new FormControl,
    email: new FormControl
  })

  constructor(private httpClient: HttpClient,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    console.log("id", this.route.snapshot.params['id']);
    if (this.route.snapshot.params['id'] !== undefined) {
      this.httpClient.get("/api/customer/" + this.route.snapshot.params['id']).subscribe((response: CustomerDto) => {
        console.log(response);
        this.id = this.route.snapshot.params['id'];
        this.name = response.name;
        this.cui = response.cui;
        this.address = response.address;
        this.telephone = response.telephone;
        this.email = response.email;
      })
    }

  }

  saveCustomer() {
    var customer = {
      id: this.id,
      name: this.name,
      cui: this.cui,
      address: this.address,
      telephone: this.telephone,
      email: this.email
    }
    this.httpClient.post("/api/customer", customer).subscribe((response) => {
      console.log(response);
      alert("Customer was saved");
      this.router.navigate(["/customer-list"]);
    })
  }



}
