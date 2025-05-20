import {Component, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree,} from "@angular/router";
import {AuthenticationService} from "./AuthenticationService";




@Component({
  selector: 'app-auth-guard',
  templateUrl: './auth-guard.component.html',
  styleUrls: ['./auth-guard.component.css']
})

@Injectable({
  providedIn: 'root'
})
export class AuthGuardComponent implements CanActivate{

  constructor(private authService: AuthenticationService, private router: Router) {
  }

  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }

  }

}
