import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {DocumentService} from "../services/document.service";
import {InterventionSheetListComponent} from "../intervention-sheet-list/intervention-sheet-list.component";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-document-dialog',
  templateUrl: './document-dialog.component.html',
  styleUrls: ['./document-dialog.component.css']
})
export class DocumentDialogComponent {

  documents: any[] = [];
  selectedDocumentUrl: string | null = null;
  private apiUrl = 'http://188.24.7.49:8080/api/interventions';
  selectedFile: File | null = null;
  previewUrl: SafeResourceUrl | null = null;
  loadingPreview: boolean = false;



  constructor(private documentService: DocumentService, private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: { intervention: any },
  private sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.documentService.getDocuments(this.data.intervention.id).subscribe({
      next: (docs) => {
        this.documents = docs.map((doc) => ({
          ...doc,
          url: `http://188.24.7.49:8080/api/interventions/${this.data.intervention.id}/documents/${doc.name}`
        }));
      },
      error: (err) => console.error('Error loading documents', err)
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.documentService.uploadDocument(this.data.intervention.id, file).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => console.error('Error uploading file', err)
      });
    }
  }

  deleteDocument(filename: string): void {
    if(confirm("Sure you want delete it?")) {
      this.documentService.deleteDocument(this.data.intervention.id, filename).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => console.error('Delete failed', err)
      });
    }
  }

  // previewDocument(doc: any): void {
  //   const url = `http://188.24.7.49:8080/api/interventions/${this.data.intervention.id}/documents/${doc.name}`;
  //   window.open(url, '_blank');
  // }
  previewDocument(doc: any): void {
    this.documentService.downloadDocument(this.data.intervention.id, doc.name).subscribe({
      next: (blob) => {
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
      },
      error: (err) => {
        console.error('Preview failed:', err);
        alert('Nu s-a putut deschide PDF-ul');
      }
    });
  }


  // downloadDocument(doc: any) {
  //   this.documentService.downloadDocument(this.data.intervention.id, doc.name).subscribe({
  //     next: (blob) => {
  //       const url = window.URL.createObjectURL(blob);
  //       const a = document.createElement('a');
  //       a.href = url;
  //       a.download = doc.name;
  //       a.click();
  //       window.URL.revokeObjectURL(url);
  //     },
  //     error: (err) => {
  //       console.error('Error downloading file:', err);
  //       alert('Eroare la descărcarea fișierului!');
  //     }
  //   });
  // }
  downloadDocument(id: number, filename: string) {
    const url = `${this.apiUrl}/${id}/documents/${filename}`;
    return this.http.get(url, {
      responseType: 'blob',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    });
  }


  protected readonly InterventionSheetListComponent = InterventionSheetListComponent;
}
