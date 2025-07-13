import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthenticationService} from "../auth-guard/AuthenticationService";

@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private apiUrl = 'http://188.24.7.49:8080/api/interventions'; // modifică dacă e nevoie

  constructor(private http: HttpClient, private authService: AuthenticationService) {
  }

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  uploadDocument(interventionId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/${interventionId}/documents`, formData, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    });
  }

  getDocuments(interventionId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${interventionId}/documents`, {
      headers: this.getAuthHeaders()
    });
  }

  deleteDocument(interventionId: number, filename: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${interventionId}/documents/${filename}`, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    });
  }

  getDocumentFile(interventionId: number, filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${interventionId}/documents/${filename}`, {
      headers: this.getAuthHeaders(),
      responseType: 'blob'
    });
  }

  downloadDocument(interventionId: number, filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${interventionId}/documents/${filename}`, {
      headers: this.getAuthHeaders(),
      responseType: 'blob' // primești fișierul ca Blob
    });
  }
}
