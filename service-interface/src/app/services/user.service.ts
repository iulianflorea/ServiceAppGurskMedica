import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from '../dtos/userDto';
import { environment } from '../../environments/environment';

export interface PasswordResetDto {
  userId: number;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = `${environment.apiUrl}/user`;

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  findAll(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.apiUrl}/findAll`, {
      headers: this.getAuthHeaders()
    });
  }

  resetPassword(userId: number, newPassword: string): Observable<void> {
    const dto: PasswordResetDto = { userId, newPassword };
    return this.http.post<void>(`${this.apiUrl}/admin/reset-password`, dto, {
      headers: this.getAuthHeaders()
    });
  }
}
