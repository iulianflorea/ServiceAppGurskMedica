import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthenticationService } from "./auth-guard/AuthenticationService";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'service-interface';
  showHeader = true;

  private readonly HIDDEN_HEADER_ROUTES = ['/ticket-form'];

  constructor(private authenticationService: AuthenticationService, private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.showHeader = !this.HIDDEN_HEADER_ROUTES.includes(event.urlAfterRedirects.split('?')[0]);
      }
    });
  }

  ngOnInit() {
    this.authenticationService.checkTokenOnInit();
  }
}
