import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Obține token-ul de autentificare din localStorage sau din altă sursă
    const authToken = localStorage.getItem('token');

    // Verifică dacă există token și adaugă-l în antetul cererii
    if (authToken) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${authToken}`
        }
      });
    }

    // Treci cererea la următorul interceptor sau la obiectul de manipulare HTTP fără modificări
    return next.handle(request);
  }
}
