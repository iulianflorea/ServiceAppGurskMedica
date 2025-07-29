import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable} from "rxjs";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  isDarkMode = false;
  private backupUrl = 'http://188.24.7.49:8080/api/backup';
  private backupUrlDatabase = 'http://188.24.7.49:8080/api/backup/database';
  backupStatus: string = '';
  backupStatusDatabase: string = '';
  constructor(private http: HttpClient, private router: Router) {
  }
  logout() {
    localStorage.clear();
    this.router.navigate(["/login"]);
  }

  isMobile: boolean = false;

  ngOnInit() {
    this.checkMobile();
    window.addEventListener('resize', this.checkMobile.bind(this));

    const savedTheme = localStorage.getItem('darkMode');
    this.isDarkMode = savedTheme === 'true';
    this.applyTheme();
  }

  checkMobile() {
    this.isMobile = window.innerWidth < 768;
  }


  toggleDarkMode(event: any): void {
    this.isDarkMode = event.checked;
    localStorage.setItem('darkMode', String(this.isDarkMode));
    this.applyTheme();
  }

  applyTheme(): void {
    const body = document.body.classList;
    if (this.isDarkMode) {
      body.add('dark-theme');
    } else {
      body.remove('dark-theme');
    }
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
