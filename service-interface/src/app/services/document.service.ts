import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  apiUrl = 'http://188.24.7.49:8080/api/interventions';

  constructor(private http: HttpClient) {}

  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  getDocuments(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/documents`, {
      headers: this.getAuthHeaders()
    });
  }

  uploadDocument(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${id}/documents`, formData, {
      headers: this.getAuthHeaders()
    });
  }

  deleteDocument(id: number, filename: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}/documents/${filename}`, {
      headers: this.getAuthHeaders()
    });
  }


  downloadDocument(interventionId: number, filename: string): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    const url = `${this.apiUrl}/${interventionId}/documents/${filename}`;
    return this.http.get(url, {
      headers: headers,
      responseType: 'blob'
    });
  }

}
