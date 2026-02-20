import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class VehicleDocumentService {

  constructor(private http: HttpClient) {}

  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getDocuments(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/api/vehicles/${id}/documents`, {
      headers: this.getAuthHeaders()
    });
  }

  uploadDocument(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${environment.apiUrl}/api/vehicles/${id}/documents`, formData, {
      headers: this.getAuthHeaders()
    });
  }

  deleteDocument(id: number, filename: string): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/api/vehicles/${id}/documents/${filename}`, {
      headers: this.getAuthHeaders()
    });
  }

  downloadDocument(id: number, filename: string): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    const url = `${environment.apiUrl}/api/vehicles/${id}/documents/${filename}`;
    return this.http.get(url, { headers, responseType: 'blob' });
  }

  getPhoto(id: number): Observable<Blob> {
    return this.http.get(`${environment.apiUrl}/api/vehicles/${id}/photo`, {
      headers: this.getAuthHeaders(),
      responseType: 'blob'
    });
  }
}
