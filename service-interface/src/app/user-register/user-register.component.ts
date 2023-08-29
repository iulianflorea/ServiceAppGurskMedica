import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-user-register',
  templateUrl: './user-register.component.html',
  styleUrls: ['./user-register.component.css']
})
export class UserRegisterComponent {

  registerForm: FormGroup = new FormGroup({
    email: new FormControl(),
    pass: new FormControl()
  });

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  register() {
    var registerBody = {
      email: this.registerForm.value.email,
      pass: this.registerForm.value.pass
    };

    this.httpClient.post("/api/register", registerBody).subscribe(
      registered => {
        localStorage.setItem("RegisteredInUserEmail", registerBody.email);
        localStorage.setItem("RegisteredInUserPass", registerBody.pass);
      },
      error => {
        alert("Ceva a mers prost");
      }
    )
  }

}
