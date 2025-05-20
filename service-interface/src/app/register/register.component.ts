import { Component } from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatButtonModule} from "@angular/material/button";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule, ReactiveFormsModule, MatButtonModule]
})
export class RegisterComponent {

  id: any;
  firstname: any;
  lastname: any;
  email: any;
  password: any;



  constructor(private httpClient: HttpClient, private router: Router) {
  }

  registerForm : FormGroup = new FormGroup({
    firstname: new FormControl,
    lastname: new FormControl,
    email: new FormControl,
    password: new FormControl
  })


  register() {
    var registerBody = {
      id: this.registerForm.value.id,
      firstname: this.registerForm.value.firstname,
      lastname: this.registerForm.value.lastname,
      email: this.registerForm.value.email,
      password: this.registerForm.value.password
    }
    this.httpClient.post( "/api/api/v1/auth/register", registerBody).subscribe((response) => {
      console.log(response);
      alert("You are registered now");
      this.router.navigate([""]);
    })
  }

}
