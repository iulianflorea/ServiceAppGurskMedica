
import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  // constructor() { }
  //
  // isLoggedIn(): boolean {
  //   // Implementează logica de verificare a existenței și validității tokenului aici
  //   return !!localStorage.getItem('token');
  // }
  //
  // getToken(): string | null {
  //   return localStorage.getItem('token');
  // }
  private tokenKey = 'token';
  private logoutTimer: any;

  constructor(private router: Router) {}

  // Salvează tokenul în localStorage și pornește timerul de auto-logout
  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
    this.setAutoLogout(token);
  }

  // Returnează tokenul curent
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  // Verifică dacă tokenul este expirat
  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp;
      const now = Math.floor(Date.now() / 1000);
      return now >= expiry;
    } catch (e) {
      return true;
    }
  }

  // Auto-logout după expirarea tokenului
  setAutoLogout(token: string): void {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expiry = payload.exp * 1000;
    const now = Date.now();
    const timeout = expiry - now;

    if (this.logoutTimer) {
      clearTimeout(this.logoutTimer);
    }

    if (timeout > 0) {
      this.logoutTimer = setTimeout(() => {
        this.logout();
      }, timeout);
    } else {
      this.logout(); // dacă deja expirat
    }
  }

  // Logout manual sau automat
  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['/login']);
  }

  // Verifică dacă e logat și tokenul este valid
  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  }

  // Poți apela asta în AppComponent la start
  checkTokenOnInit(): void {
    const token = this.getToken();
    if (token) {
      if (this.isTokenExpired(token)) {
        this.logout();
      } else {
        this.setAutoLogout(token);
      }
    }
  }
}
