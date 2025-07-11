import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  isDarkMode = false;
  constructor(private httpClient: HttpClient, private router: Router) {
  }
  logout() {
    localStorage.clear();
    this.router.navigate(["/login"]);
  }

  isMobile: boolean = false;

  ngOnInit() {
    this.checkMobile();
    window.addEventListener('resize', this.checkMobile.bind(this));

    const savedTheme = localStorage.getItem('darkMode');
    this.isDarkMode = savedTheme === 'true';
    this.applyTheme();
  }

  checkMobile() {
    this.isMobile = window.innerWidth < 768;
  }


  toggleDarkMode(event: any): void {
    this.isDarkMode = event.checked;
    localStorage.setItem('darkMode', String(this.isDarkMode));
    this.applyTheme();
  }

  applyTheme(): void {
    const body = document.body.classList;
    if (this.isDarkMode) {
      body.add('dark-theme');
    } else {
      body.remove('dark-theme');
    }
  }

}
