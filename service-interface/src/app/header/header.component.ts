import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  constructor(private httpClient: HttpClient, private router: Router) {
  }
  logout() {
    localStorage.clear();
    this.router.navigate(["/login"]);
  }

}
