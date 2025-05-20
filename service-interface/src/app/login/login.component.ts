import { Component } from '@angular/core';
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email: string = "";
  password: string = "";
  errorMessage: string = "";

  constructor(private authService: AuthService, private router: Router) {}

  loginForm:FormGroup = new FormGroup({
    email: new FormControl,
    password: new FormControl
  })

  login(): void {

    var loginBody = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    }
    this.authService.login(loginBody.email, loginBody.password)
      .subscribe(
        response => {
          localStorage.setItem('token', response.token);
          this.router.navigate(['']);
          console.log(response);
        },
        error => {
          this.errorMessage = 'Autentificare eșuată. Verificați numele de utilizator și parola.';
        }
      );
  }

  logout() {
    localStorage.clear();
  }

}
