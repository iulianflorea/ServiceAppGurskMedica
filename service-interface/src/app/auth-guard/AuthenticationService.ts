
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor() { }

  isLoggedIn(): boolean {
    // Implementează logica de verificare a existenței și validității tokenului aici
    return !!localStorage.getItem('token');
  }
}
