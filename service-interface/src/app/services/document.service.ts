import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthenticationService} from "../auth-guard/AuthenticationService";

@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private apiUrl = 'http://localhost:8080/api/interventions'; // modifică dacă e nevoie

  constructor(private http: HttpClient, private authService: AuthenticationService) {
  }

  uploadDocument(interventionId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/${interventionId}/documents`, formData, { responseType: 'text' });
  }

  getDocuments(interventionId: number): Observable<any[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
    return this.http.get<any[]>(`${this.apiUrl}/${interventionId}/documents`);
  }

  deleteDocument(interventionId: number, filename: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${interventionId}/documents/${filename}`,{ responseType: 'text' });
  }
}
