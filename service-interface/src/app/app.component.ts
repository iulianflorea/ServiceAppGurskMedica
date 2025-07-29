import { Component } from '@angular/core';
import {AuthenticationService} from "./auth-guard/AuthenticationService";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'service-interface';

  constructor(private authenticationService: AuthenticationService) {
  }
ngOnInit(){
    this.authenticationService.checkTokenOnInit();
}

}
