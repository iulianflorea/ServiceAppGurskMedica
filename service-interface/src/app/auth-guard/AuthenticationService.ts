
import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  role?: string;           // dacă ai un singur rol ca string
  roles?: string[];        // sau dacă ai roluri multiple într-un array
  [key: string]: any;      // alte câmpuri posibile
}

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private tokenKey = 'token';
  private logoutTimer: any;
  private decodedToken: any;

  constructor(private router: Router) {
  }

  private getDecodedToken(): JwtPayload | null {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return null;

    try {
      return jwtDecode<JwtPayload>(token);
    } catch (e) {
      console.error('Invalid token:', e);
      return null;
    }
  }

  isAdmin(): boolean {
    const decoded = this.getDecodedToken();
    if (!decoded) return false;

    // Verifică rolul, adaptând după cum e în JWT-ul tău
    // Dacă ai rolul ca string:
    if (decoded.role) {
      return decoded.role.toUpperCase() === 'ADMIN';
    }

    // Dacă ai roluri într-un array, ex: roles: ["ROLE_ADMIN", "ROLE_USER"]
    if (decoded.roles && Array.isArray(decoded.roles)) {
      return decoded.roles.some(r => r.toUpperCase().includes('ADMIN'));
    }

    return false;
  }

  public getRoles(): string[] {
    return this.decodedToken?.roles || this.decodedToken?.authorities || [];
  }



  public isUser(): boolean {
    return this.getRoles().includes('ROLE_USER');
  }

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
