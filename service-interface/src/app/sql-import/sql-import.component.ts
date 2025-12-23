import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "../auth-guard/AuthenticationService";
import {FormControl, FormGroup} from "@angular/forms";
import {BackupDto} from "../dtos/bacupDto";
import {Observable} from "rxjs";


declare global {
  interface Window {
    electron: {
      invoke: (channel: string, ...args: any[]) => Promise<any>;
    };
  }
}
@Component({
  selector: 'app-sql-import',
  templateUrl: './sql-import.component.html',
  styleUrls: ['./sql-import.component.css']
})
export class SqlImportComponent implements OnInit{
  selectedFile: File | null = null;
  userIsAdmin: boolean = false;
  id: any;
  sqlPath: any;
  documentPath: any;

  // private backupUrl = 'http://localhost:8080/api/backup/manual';
  // private backupUrl = 'http://188.24.7.49:8080/api/backup/manual';
  private backupUrl = 'https://gursk.singularity-cloud.com/api/backup/manual';
  // private backupUrlDatabase = 'http://localhost:8080/api/backup/database';
  // private backupUrlDatabase = 'http://188.24.7.49:8080/api/backup/database';
  private backupUrlDatabase = 'https://gursk.singularity-cloud.com/api/backup/database';
  backupStatus: string = '';
  backupStatusDatabase: string = '';

  constructor(private http: HttpClient, private authService: AuthenticationService) {
    this.userIsAdmin = this.authService.isAdmin();
  }

  pathsForm: FormGroup = new FormGroup({
    sqlPath: new FormControl,
    documentPath: new FormControl
    }
  )

  ngOnInit() {
    this.http.get<BackupDto>("/api/backup/admin/findById/1").subscribe((response) => {
      this.id = response.id;
      this.pathsForm.patchValue({
        sqlPath: response.sqlPath,
        documentPath: response.documentPath
      });
    });
  }

  savePaths() {
    const paths = {
      id: 1,
      sqlPath: this.pathsForm.value.sqlPath,
      documentPath: this.pathsForm.value.documentPath
    };

    this.http.post("/api/backup/admin/set-path", paths).subscribe((response) => {
      console.log("Response:", response);
      alert("paths was saved");
    });
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

  backupManualDbDoc(){
    this.runBackup();
    this.runBackupDatabase();
  }

  runManualBackup(): Observable<string> {
    return this.http.post(this.backupUrl, null, { responseType: 'text' });
  }
  runManualBackupDatabase(): Observable<string> {
    return this.http.post(this.backupUrlDatabase, null, { responseType: 'text' });
  }

  runBackup() {
    this.backupStatus = 'Backup în curs...';

    this.runManualBackup().subscribe({
      next: (res) => {
        // this.backupStatus = 'Backup documente realizat cu succes.';
        alert("Backup documente realizat cu succes.")
      },
      error: () => {
        // this.backupStatus = 'Eroare la backup documente.';
        alert("Eroare la backup documente.")
      }
    });
  }

  runBackupDatabase() {
    this.backupStatusDatabase = 'Backup baza de date în curs...';

    this.runManualBackupDatabase().subscribe({
      next: (res) => {
        // this.backupStatusDatabase = 'Backup baza de date realizat cu succes.';
        alert("Backup baza de date realizat cu succes.");
      },
      error: () => {
        // this.backupStatusDatabase = 'Eroare la backup-ul bazei de date.';
        alert("Eroare la backup-ul bazei de date.");
      }
    });
  }

}
