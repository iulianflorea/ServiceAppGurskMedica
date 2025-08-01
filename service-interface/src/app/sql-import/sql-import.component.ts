import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "../auth-guard/AuthenticationService";

@Component({
  selector: 'app-sql-import',
  templateUrl: './sql-import.component.html',
  styleUrls: ['./sql-import.component.css']
})
export class SqlImportComponent {
  selectedFile: File | null = null;
  userIsAdmin: boolean = false;
  constructor(private http: HttpClient, private authService: AuthenticationService) {
    this.userIsAdmin = this.authService.isAdmin();
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];
    }
  }

  onSubmit(): void {
    if (!this.selectedFile) return;

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post('/api/database/sql-import', formData, {responseType: 'text'})
      .subscribe({
        next: () => alert('Import successful!'),
        error: (err) => alert('Import failed: ' + err.message)
      });
  }
}
