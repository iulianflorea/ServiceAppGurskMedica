import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  isUserLoggedIn(): boolean{
    return localStorage.getItem("loggedInUserEmail") != null
      && localStorage.getItem("loggedInUserPass") != null;
  }
  logoutUser(){
    localStorage.removeItem("loggedInUserEmail");
    localStorage.removeItem("loggedInUserPass");
  }
}
