import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent {

  loginForm: FormGroup = new FormGroup({
    email: new FormControl(),
    password: new FormControl()
  });

  constructor(private httpClient: HttpClient, private router: Router) {

  }

  login() {
    var loginBody = {
      email: this.loginForm.value.email,
      pass: this.loginForm.value.password
    };

    this.httpClient.post("/api/login", loginBody).subscribe(
      successR => {
        localStorage.setItem("loggedInUserEmail", loginBody.email);
        localStorage.setItem("loggedInUserPass", loginBody.pass);
        this.router.navigate(["/home"]);
      },
      errorR => {
        alert("Ceva a mers prost");
      }
    );
  }

}
